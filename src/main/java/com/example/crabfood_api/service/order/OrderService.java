package com.example.crabfood_api.service.order;

import com.example.crabfood_api.dto.request.CartItemDTO;
import com.example.crabfood_api.dto.request.OptionChoiceDTO;
import com.example.crabfood_api.dto.request.OrderRequest;
import com.example.crabfood_api.dto.response.OrderResponse;
import com.example.crabfood_api.dto.response.VNPayResponse;
import com.example.crabfood_api.dto.websocket.OrderTrackingDTO;
import com.example.crabfood_api.exception.ResourceNotFoundException;
import com.example.crabfood_api.model.entity.*;
import com.example.crabfood_api.model.enums.*;
import com.example.crabfood_api.repository.*;
import com.example.crabfood_api.service.AbstractCrudService;
import com.example.crabfood_api.service.VNPay.VNPayService;
import com.example.crabfood_api.util.DateTimeHelper;
import com.example.crabfood_api.util.Mapper;
import com.example.crabfood_api.util.UserUtil;
import com.example.crabfood_api.util.WebSocketService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService
        extends AbstractCrudService<OrderRequest, OrderResponse, OrderRepository, Order, Long>
        implements IOrderService {
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final AddressRepository addressRepository;
    private final FoodOptionRepository foodOptionRepository;
    private final VNPayService vnPayService;
    private final PaymentRepository paymentRepository;
    private final UserUtil userUtil;
    private final WebSocketService webSocketService;
    private final RiderProfileRepository riderProfileRepository;
    private final RiderAssignmentService riderAssignmentService;

    protected OrderService(OrderRepository repository,
                           FoodRepository foodRepository, UserRepository userRepository,
                           VendorRepository vendorRepository, AddressRepository addressRepository,
                           FoodOptionRepository foodOptionRepository, VNPayService vnPayService,
                           PaymentRepository paymentRepository, UserUtil userUtil,
                           WebSocketService webSocketService, RiderProfileRepository riderProfileRepository,
                           RiderAssignmentService riderAssignmentService) {
        super(repository, Order.class);
        this.foodRepository = foodRepository;
        this.userRepository = userRepository;
        this.vendorRepository = vendorRepository;
        this.addressRepository = addressRepository;
        this.foodOptionRepository = foodOptionRepository;
        this.vnPayService = vnPayService;
        this.paymentRepository = paymentRepository;
        this.userUtil = userUtil;
        this.webSocketService = webSocketService;
        this.riderProfileRepository = riderProfileRepository;
        this.riderAssignmentService = riderAssignmentService;
    }
    @Override
    public OrderResponse getOrder(Long orderId, Long customerId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new AccessDeniedException("You don't have permission to view this order");
        }

        return toResponse(order);
    }

    @Override
    public List<OrderResponse> getCustomerOrders(Long customerId) {
        List<Order> orders = repository.findByCustomerId(customerId);
        return orders.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> getVendorOrders(Long vendorId) {
        List<Order> orders = repository.findByVendorId(vendorId);
        return orders.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> getCustomerOrdersUpcoming(Long customerId) {
        List<Order> upcoming = repository.findOrdersByCustomerAndStatuses(customerId,
                List.of(
                        OrderStatus.PENDING,
                        OrderStatus.ACCEPTED,
                        OrderStatus.PICKED_UP,
                        OrderStatus.ON_THE_WAY
                )
        );

        return upcoming.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> getCustomerOrdersHistory(Long customerId) {
        List<Order> history = repository.findOrdersByCustomerAndStatuses(customerId,
                List.of(
                        OrderStatus.SUCCESS,
                        OrderStatus.CANCELLED
                )
        );

        return history.stream()
                .map(this::toResponse)
                .toList();
    }

    // Các phương thức hỗ trợ
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }

    @Transactional
    @Override
    public OrderResponse updateOrderStatus(Long orderId, String status, Long userId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Kiểm tra quyền truy cập
        validateOrderUpdatePermission(user, order);

        // Chuyển đổi status an toàn
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }

        // Cập nhật trạng thái đơn hàng
        order.setOrderStatus(newStatus);
        order.getStatusHistory().add(OrderStatusHistory.builder()
                .order(order)
                .status(newStatus)
                .changedBy(user)
                .notes("Cập nhật trạng thái đơn hàng thành " + newStatus.name())
                .build());

        OrderResponse response = toResponse(repository.save(order));
        webSocketService.sendOrderStatusChanged(response);
        return response;
    }

    private void validateOrderUpdatePermission(User user, Order order) {
        Set<Role> roles = user.getRoles().stream()
                .map(UserRole::getRole)
                .collect(Collectors.toSet());

        if (roles.contains(Role.VENDOR)) {
            if (!order.getVendor().getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("Bạn không có quyền cập nhật đơn hàng này (VENDOR).");
            }
        }

        if (roles.contains(Role.RIDER)) {
            if (order.getRider() == null || !order.getRider().getId().equals(user.getRiderProfile().getId())) {
                throw new AccessDeniedException("Bạn không có quyền cập nhật đơn hàng này (RIDER).");
            }
        }
    }

    @Transactional
    @Override
    public OrderResponse cancelOrder(Long orderId, String reason, Long customerId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new AccessDeniedException("You don't have permission to cancel this order");
        }
        if (order.getOrderStatus() == OrderStatus.PENDING || order.getOrderStatus() == OrderStatus.ACCEPTED) {
            // Cập nhật trạng thái đơn hàng
            order.setOrderStatus(OrderStatus.CANCELLED);
            order.setCancellationReason(reason);
            order.getStatusHistory().add(OrderStatusHistory.builder()
                    .order(order)
                    .status(OrderStatus.CANCELLED)
                    .changedBy(customer)
                    .notes("Cập nhật trạng thái đơn hàng thành cancel")
                    .build());
        } else {
            return new OrderResponse();
        }

        // Lưu vào database
        Order updatedOrder = repository.save(order);
        OrderResponse response = toResponse(updatedOrder);
        webSocketService.sendOrderUpdated(response);
        return response;
    }

    @Transactional
    public VNPayResponse createPaymentUrl(Long orderId) {
        Order order = repository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        BigDecimal totalPrice = order.getTotalAmount();
        BigDecimal rounded = totalPrice.setScale(2, RoundingMode.HALF_UP);
        long amountForVNPay = rounded.longValue();
        String paymentUrl = vnPayService.createPaymentUrl(
                order.getId(),
                amountForVNPay,
                "Payment for order " + order.getOrderNumber()
        );

        // 6. Update order status to PENDING_PAYMENT
        order.setPaymentStatus(OrderPaymentStatus.PENDING);
        repository.save(order);
        return new VNPayResponse(order.getId(), "Order created successfully", OrderPaymentStatus.PENDING, paymentUrl);
    }

    @Transactional
    public VNPayResponse processPaymentCallback(Map<String, String> vnpayResponse) {
        String vnp_ResponseCode = vnpayResponse.get("vnp_ResponseCode");
        String vnp_TxnRef = vnpayResponse.get("vnp_TxnRef");
        Long orderId = Long.parseLong(vnp_TxnRef.split("-")[0]);
        LocalDateTime vnp_PayDate = DateTimeHelper.convertToLocalDateTime(vnpayResponse.get("vnp_PayDate"));
        String vnp_TransactionNo = vnpayResponse.get("vnp_TransactionNo");
        String vnp_BankCode = vnpayResponse.get("vnp_BankCode");
        String vnp_OrderInfo = vnpayResponse.get("vnp_OrderInfo");

        Order order = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("00".equals(vnp_ResponseCode)) {
            // Payment successful
            order.setPaymentStatus(OrderPaymentStatus.PAID);
            order.setOrderStatus(OrderStatus.ACCEPTED);

            createVNPayTransaction(vnpayResponse, vnp_ResponseCode,
                    vnp_PayDate, vnp_TransactionNo, vnp_BankCode,
                    vnp_OrderInfo, order, PaymentStatus.SUCCESS);

            VNPayResponse vnPayResponse = new VNPayResponse(orderId, "Payment successful", OrderPaymentStatus.PAID);
            vnPayResponse.setPaymentDetails(new VNPayResponse.PaymentDetails(vnpayResponse.get("vnp_TransactionNo"),
                    "VNPay", Long.parseLong(vnpayResponse.get("vnp_Amount")) / 100L, LocalDateTime.now()));
            return vnPayResponse;
        } else {
            // Payment failed
            createVNPayTransaction(vnpayResponse, vnp_ResponseCode, vnp_PayDate,
                    vnp_TransactionNo, vnp_BankCode, vnp_OrderInfo, order, PaymentStatus.FAILED);
            order.setPaymentStatus(OrderPaymentStatus.FAILED);
            return new VNPayResponse(orderId, "Payment failed", OrderPaymentStatus.FAILED);
        }
    }

    private void createVNPayTransaction(Map<String, String> vnpayResponse, String vnp_ResponseCode,
                                        LocalDateTime vnp_PayDate, String vnp_TransactionNo,
                                        String vnp_BankCode, String vnp_OrderInfo,
                                        Order order, PaymentStatus paymentStatus) {
        VNPayTransaction payment = new VNPayTransaction();
        payment.setOrder(order);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setResponseCode(vnp_ResponseCode);
        payment.setTransactionInfo(vnp_OrderInfo);
        payment.setBankCode(vnp_BankCode);
        payment.setPaymentDate(vnp_PayDate);
        payment.setVnpayTransactionNo(vnp_TransactionNo);
        payment.setAmount(Double.parseDouble(vnpayResponse.get("vnp_Amount")) / 100L);
        payment.setPaymentMethod(PaymentMethod.VNPAY);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setTransactionId(vnpayResponse.get("vnp_TransactionNo"));
        paymentRepository.save(payment);
    }

    @Transactional
    @Override
    protected Order createAndSave(OrderRequest request) {
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        Address address = addressRepository.findById(request.getDeliveryAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        // Tạo order
        Order order = new Order();
        order.setCustomer(customer);
        order.setVendor(vendor);
        order.setOrderNumber(generateOrderNumber());
        order.setSubtotal(request.getSubtotal());
        order.setDeliveryFee(request.getDeliveryFee());
        order.setDiscountAmount(request.getDiscountAmount());
        order.setDeliveryAddress(address);
        order.setDeliveryAddressText(request.getDeliveryAddressText());
        order.setDeliveryNotes(request.getDeliveryNotes());
        order.setDeliveryLatitude(request.getDeliveryLatitude());
        order.setDeliveryLongitude(request.getDeliveryLongitude());
        order.setTotalAmount(BigDecimal.valueOf(order.getSubtotal() + order.getDeliveryFee() - order.getDiscountAmount()));
        order.setPaymentMethod(OrderPaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));
        order.setOrderStatus(OrderStatus.PENDING);
        order.setCreatedBy(customer);
        order.setRecipientName(request.getRecipientName());
        order.setRecipientPhone(request.getRecipientPhone());

        // Auto assign rider using priority scoring
        RiderProfile assignedRider = riderAssignmentService.findBestRider(vendor);
        if (assignedRider != null) {
            order.setRider(assignedRider);
            assignedRider.setStatus(RiderStatus.DELIVERING);
            riderProfileRepository.save(assignedRider);
        }

        order.getStatusHistory().add(OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.PENDING)
                .changedBy(customer)
                .notes("Đang đợi xác nhận.")
                .build());

        // Thêm các orderFood
        for (CartItemDTO item : request.getItems()) {
            Food food = foodRepository.findById(item.getFoodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

            OrderFood orderFood = new OrderFood();
            orderFood.setOrder(order);
            orderFood.setFood(food);
            orderFood.setFoodName(food.getName());
            orderFood.setQuantity(item.getQuantity());
            orderFood.setFoodPrice(food.getPrice());


            List<OptionChoiceDTO> choices = item.getSelectedOptions();
            // Thêm các choice
            for (OptionChoiceDTO choice : choices) {
                FoodOption foodOption = foodOptionRepository.findById(choice.getOptionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Food Option not found"));
                OrderFoodChoice orderFoodChoice = new OrderFoodChoice();
                orderFoodChoice.setOrderFood(orderFood);
                orderFoodChoice.setOptionName(foodOption.getName());
                orderFoodChoice.setChoiceName(choice.getName());
                orderFoodChoice.setPriceAdjustment(choice.getPriceAdjustment());
                orderFoodChoice.setOptionId(choice.getOptionId());
                orderFood.getChoices().add(orderFoodChoice);
            }

            order.getOrderFoods().add(orderFood);
        }

        // Lưu vào database
        return repository.save(order);
    }

    @Override
    public OrderResponse create(OrderRequest request) {
        OrderResponse response = toResponse(createAndSave(request));
        webSocketService.sendOrderCreated(response);
        return response;
    }

    @Override
    public List<OrderResponse> findAll() {
        List<Order> orders = repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return orders.stream().map(this::toResponse).toList();
    }

    @Override
    protected Order updateAndSave(Long id, OrderRequest request) {
        return null;
    }

    @Override
    protected OrderResponse toResponse(Order domainEntity) {
        return Mapper.toOrderResponse(domainEntity);
    }

    @Override
    public OrderTrackingDTO getOrderTrackingInfo(Long orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return OrderTrackingDTO.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getOrderStatus())
                .riderLatitude(order.getRider() != null ? order.getRider().getCurrentLatitude() : null)
                .riderLongitude(order.getRider() != null ? order.getRider().getCurrentLongitude() : null)
                .riderName(order.getRider() != null ? order.getRider().getUser().getFullName() : null)
                .riderPhone(order.getRider() != null ? order.getRider().getUser().getPhone() : null)
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime() != null ? 
                    order.getEstimatedDeliveryTime().toString() : null)
                .currentLocation(order.getDeliveryAddressText())
                .nextLocation(order.getVendor().getAddress())
                .build();
    }
}