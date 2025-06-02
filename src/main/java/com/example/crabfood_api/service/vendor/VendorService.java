package com.example.crabfood_api.service.vendor;


import com.example.crabfood_api.dto.request.RegisterVendorRequest;
import com.example.crabfood_api.dto.request.VendorRequest;
import com.example.crabfood_api.dto.request.VendorSearchRequest;
import com.example.crabfood_api.dto.response.*;
import com.example.crabfood_api.exception.BadRequestException;
import com.example.crabfood_api.exception.EmailException;
import com.example.crabfood_api.exception.ResourceNotFoundException;
import com.example.crabfood_api.model.entity.*;
import com.example.crabfood_api.model.enums.OrderStatus;
import com.example.crabfood_api.model.enums.Role;
import com.example.crabfood_api.repository.CategoryRepository;
import com.example.crabfood_api.repository.FoodRepository;
import com.example.crabfood_api.repository.UserRepository;
import com.example.crabfood_api.repository.VendorRepository;
import com.example.crabfood_api.service.AbstractCrudService;
import com.example.crabfood_api.service.email.IEmailService;
import com.example.crabfood_api.util.DistanceUtils;
import com.example.crabfood_api.util.Mapper;
import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.crabfood_api.util.LocationHelper.createPoint;

@Service
public class VendorService extends AbstractCrudService<VendorRequest, VendorResponse, VendorRepository, Vendor, Long> implements IVendorService {

    private final CategoryRepository categoryRepository;
    private final FoodRepository foodRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final IEmailService emailService;

    protected VendorService(VendorRepository repository, CategoryRepository categoryRepository,
                            FoodRepository foodRepository,
                            PasswordEncoder passwordEncoder, UserRepository userRepository, IEmailService emailService) {
        super(repository, Vendor.class);
        this.categoryRepository = categoryRepository;
        this.foodRepository = foodRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    public PageResponse<VendorResponse> searchByNameOrAddress(VendorSearchRequest request) {
        String keyword = request.getKeyword();
        int limit = request.getLimit();
        int offset = request.getOffset();
        Page<Vendor> vendors = repository.searchByNameOrAddress(keyword, PageRequest.of(offset, limit));
        if (vendors.isEmpty()) {
            return new PageResponse<>();
        }
        var result = vendors.map(vendor -> {
            VendorResponse response = toResponse(vendor);
            // Tính khoảng cách từ người dùng đến vendor
            if (request.getLongitude() != null && request.getLatitude() != null) {
                GeometryFactory geometryFactory = new GeometryFactory();
                Point userLocation = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
                double distance = DistanceUtils.calculateDistance(userLocation, vendor.getLocation());
                response.setDistance(Math.round(distance * 100.0) / 100.0);
            } else {
                response.setDistance(0.0);
            }
            return response;
        });
        return new PageResponse<>(result);
    }

    @Override
    protected Vendor createAndSave(VendorRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createAndSave'");
    }

    @Override
    protected Vendor updateAndSave(Long id, VendorRequest request) {
        Vendor vendor = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Vendor not found"));
        vendor.setName(request.getName());
        vendor.setAddress(request.getAddress());
        vendor.setClosingTime(request.getClosingTime());
        vendor.setOpeningTime(request.getOpeningTime());
        vendor.setCoverImageUrl(request.getCoverImageUrl());
        vendor.setLogoUrl(request.getLogoUrl());
        vendor.setDeliveryFee(request.getDeliveryFee());
        vendor.setDescription(request.getDescription());
        vendor.setServiceRadiusKm(request.getServiceRadiusKm());
        vendor.setLocation(createPoint(request.getLongitude(), request.getLatitude()));
        vendor.setCuisineType(request.getCuisineType());
        return repository.save(vendor);
    }

    @Override
    protected VendorResponse toResponse(Vendor domainEntity) {
        return Mapper.toVendorResponse(domainEntity);
    }

    @Override
    public PageResponse<VendorResponse> getNearbyVendors(VendorSearchRequest request) {
        // 1. Tạo Point từ tọa độ người dùng
        GeometryFactory geometryFactory = new GeometryFactory();
        Point userLocation = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));

        // 2. Gọi repository với phân trang
        Page<Vendor> vendors = repository.findNearbyVendors(
                request,
                PageRequest.of(request.getOffset(), request.getLimit())
        );

        var result = vendors.map(vendor -> {
            VendorResponse response = toResponse(vendor);

            double distance = DistanceUtils.calculateDistance(userLocation, vendor.getLocation());
            response.setDistance(Math.round(distance * 100.0) / 100.0);
            return response;
        });

        return new PageResponse<>(result);
    }

    @Override
    public PageResponse<CategoryResponse> getCategoriesByVendorId(Long vendorId, int limit, int offset) {
        var result = categoryRepository.findByVendorIdOrGlobal(vendorId, PageRequest.of(offset, limit))
                .map(Mapper::toCategoryResponse);
        return new PageResponse<>(result);
    }

    @Override
    public PageResponse<FoodResponse> getFoodsByVendorId(Long vendorId, int limit, int offset) {
        var result = foodRepository.findByVendorId(vendorId, PageRequest.of(offset, limit))
                .map(Mapper::toFoodResponse);
        return new PageResponse<>(result);
    }

    @Override
    public VendorResponse findByUserId(Long userId) {
        return Mapper.toVendorResponse(repository.findByUserId(userId).orElseThrow(
                () -> new ResourceNotFoundException("Vendor not found")
        ));
    }

    @Override
    public VendorResponse toggleVendorStatus(Long id) {
        Vendor vendor = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Vendor not found"));
        vendor.setActive(!vendor.isActive());
        return toResponse(repository.save(vendor));
    }

    @Transactional
    @Override
    public VendorResponse approveVendor(Long id) {
        Vendor vendor = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Vendor not found"));
        vendor.setApproved(true);
        vendor.setOpen(true);
        vendor.setUpdatedAt(LocalDateTime.now());
        vendor.getUser().setEmailVerified(true);
        emailService.sendVendorApprovalNotification(
                vendor.getUser().getEmail(),
                vendor.getUser().getFullName(),
                vendor.getName(),
                true,
                ""
        );

        return toResponse(repository.save(vendor));
    }

    @Override
    public VendorResponse rejectVendor(Long id) {
        Vendor vendor = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        vendor.setApproved(false);
        vendor.setActive(false);
        repository.save(vendor);

        // Gửi email thông báo từ chối
        emailService.sendVendorApprovalNotification(
                vendor.getUser().getEmail(),
                vendor.getUser().getFullName(),
                vendor.getName(),
                false,
                ""
        );

        return toResponse(vendor);
    }

    @Override
    public VendorResponse setFavoriteVendor(Long id) {
        Vendor vendor = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Vendor not found"));
        vendor.setFavorite(!vendor.isFavorite());
        return toResponse(repository.save(vendor));
    }

    @Override
    public List<VendorResponse> findFavoriteVendors() {
        return repository.findByIsFavoriteIsTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    @Override
    public VendorRegistrationResponse registerVendor(RegisterVendorRequest request) {
        // Bước 1: Kiểm tra xem user đã tồn tại chưa
        User existingUser = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (existingUser != null) {
            // Nếu user đã tồn tại, kiểm tra xem có vendor nào liên kết không
            Optional<Vendor> existingVendor = repository.findByUserId(existingUser.getId());

            if (existingVendor.isPresent()) {
                return handleExistingVendor(existingVendor.get(), request);
            } else {
                // User tồn tại nhưng chưa có vendor profile
                return createVendorForExistingUser(existingUser, request);
            }
        }

        // Bước 2: Kiểm tra thông tin trùng lặp cho user mới
        validateUniqueConstraintsForNewUser(request);

        // Bước 3: Tạo user và vendor mới
        User user = createUser(request);
        Vendor vendor = createVendor(request, user);

        // Bước 4: Gửi email xác nhận
        sendConfirmationEmail(user, vendor);

        return VendorRegistrationResponse.builder()
                .vendorId(vendor.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .vendorName(vendor.getName())
                .status("PENDING")
                .message("Đăng ký thành công! Vui lòng chờ admin phê duyệt.")
                .build();
    }

    private VendorRegistrationResponse handleExistingVendor(Vendor existingVendor, RegisterVendorRequest request) {
        User user = existingVendor.getUser();

        // Kiểm tra trạng thái hiện tại của vendor
        if (existingVendor.isApproved() && existingVendor.isActive()) {
            throw new BadRequestException("Tài khoản vendor của bạn đã được phê duyệt và đang hoạt động.");
        }

        if (existingVendor.isApproved() && !existingVendor.isActive()) {
            throw new BadRequestException("Tài khoản vendor của bạn đã được phê duyệt nhưng đang bị tạm khóa. Vui lòng liên hệ admin.");
        }

        // Nếu vendor đang pending, cho phép cập nhật thông tin
        if (!existingVendor.isApproved() && existingVendor.isActive()) {
            updateVendorInfo(existingVendor, request);
            updateUserInfo(user, request);

            // Gửi email thông báo cập nhật
            sendResubmissionEmail(user, existingVendor);

            return VendorRegistrationResponse.builder()
                    .vendorId(existingVendor.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .vendorName(existingVendor.getName())
                    .status("PENDING")
                    .message("Thông tin đăng ký đã được cập nhật! Vui lòng chờ admin phê duyệt.")
                    .build();
        }

        // Nếu vendor bị reject (!approved && !active), cho phép đăng ký lại
        if (!existingVendor.isApproved() && !existingVendor.isActive()) {
            reactivateVendor(existingVendor, request);
            updateUserInfo(user, request);

            // Gửi email thông báo đăng ký lại
            sendReregistrationEmail(user, existingVendor);

            return VendorRegistrationResponse.builder()
                    .vendorId(existingVendor.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .vendorName(existingVendor.getName())
                    .status("PENDING")
                    .message("Đăng ký lại thành công! Thông tin của bạn đã được cập nhật và chờ phê duyệt.")
                    .build();
        }

        throw new BadRequestException("Trạng thái tài khoản không xác định. Vui lòng liên hệ admin.");
    }

    private VendorRegistrationResponse createVendorForExistingUser(User existingUser, RegisterVendorRequest request) {
        // Kiểm tra thông tin trùng lặp (trừ email đã tồn tại)
        if (!existingUser.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username đã được sử dụng");
        }

        if (!existingUser.getPhone().equals(request.getPhone()) &&
                userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Số điện thoại đã được sử dụng");
        }

        // Cập nhật thông tin user nếu cần
        updateUserInfo(existingUser, request);

        // Tạo vendor profile mới
        Vendor vendor = createVendor(request, existingUser);

        sendConfirmationEmail(existingUser, vendor);

        return VendorRegistrationResponse.builder()
                .vendorId(vendor.getId())
                .username(existingUser.getUsername())
                .email(existingUser.getEmail())
                .vendorName(vendor.getName())
                .status("PENDING")
                .message("Vendor profile đã được tạo! Vui lòng chờ admin phê duyệt.")
                .build();
    }

    private void sendConfirmationEmail(User user, Vendor vendor) {
        try {
            emailService.sendVendorRegistrationConfirmation(
                    user.getEmail(),
                    user.getFullName(),
                    vendor.getName()
            );
        } catch (Exception e) {
            throw new EmailException(String.format("Lỗi khi gửi email xác nhận cho vendor %d: %s", vendor.getId(), e.getMessage()));
        }
    }

    private void updateVendorInfo(Vendor vendor, RegisterVendorRequest request) {
        vendor.setName(request.getVendorName());
        vendor.setDescription(request.getDescription());
        vendor.setAddress(request.getAddress());
        vendor.setServiceRadiusKm(request.getServiceRadiusKm());
        vendor.setCuisineType(request.getCuisineType());
        vendor.setDeliveryFee(request.getDeliveryFee());
        vendor.setUpdatedAt(LocalDateTime.now());

        repository.save(vendor);
    }

    private void updateUserInfo(User user, RegisterVendorRequest request) {
        boolean needUpdate = false;

        if (!user.getUsername().equals(request.getUsername())) {
            user.setUsername(request.getUsername());
            needUpdate = true;
        }

        if (!user.getPhone().equals(request.getPhone())) {
            user.setPhone(request.getPhone());
            needUpdate = true;
        }

        if (!user.getFullName().equals(request.getFullName())) {
            user.setFullName(request.getFullName());
            needUpdate = true;
        }

        // Cập nhật password nếu được cung cấp
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            needUpdate = true;
        }

        if (needUpdate) {
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    private void reactivateVendor(Vendor vendor, RegisterVendorRequest request) {
        updateVendorInfo(vendor, request);
        vendor.setActive(true); // Kích hoạt lại để chờ phê duyệt
        vendor.setApproved(false); // Reset trạng thái phê duyệt
        vendor.setUpdatedAt(LocalDateTime.now());

        repository.save(vendor);
    }

    private void validateUniqueConstraintsForNewUser(RegisterVendorRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã được sử dụng");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username đã được sử dụng");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Số điện thoại đã được sử dụng");
        }
    }

    private void sendResubmissionEmail(User user, Vendor vendor) {
        try {
            emailService.sendVendorResubmissionNotification(
                    user.getEmail(),
                    user.getFullName(),
                    vendor.getName()
            );
        } catch (Exception e) {
            throw new EmailException(String.format("Lỗi khi gửi email thông báo cập nhật cho vendor %d: %s",
                    vendor.getId(), e.getMessage()));
        }
    }

    private void sendReregistrationEmail(User user, Vendor vendor) {
        try {
            emailService.sendVendorReregistrationNotification(
                    user.getEmail(),
                    user.getFullName(),
                    vendor.getName()
            );
        } catch (Exception e) {
            throw new EmailException(String.format("Lỗi khi gửi email thông báo đăng ký lại cho vendor %d: %s",
                    vendor.getId(), e.getMessage()));
        }
    }
    private User createUser(RegisterVendorRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .fullName(request.getFullName())
                .emailVerified(false)
                .phoneVerified(false)
                .isActive(true)
                .build();

        // Gán role
        UserRole userRole = new UserRole(user, Role.VENDOR);

        user.getRoles().add(userRole);

        return userRepository.save(user);
    }

    private Vendor createVendor(RegisterVendorRequest request, User user) {

        Vendor vendor = Vendor.builder()
                .user(user)
                .name(request.getVendorName())
                .description(request.getDescription())
                .address(request.getAddress())
                .serviceRadiusKm(request.getServiceRadiusKm())
                .cuisineType(request.getCuisineType())
                .deliveryFee(request.getDeliveryFee())
                .isApproved(false) // Chờ admin phê duyệt
                .isOpen(false) // Chưa mở cửa
                .isActive(true)
                .rating(0.0)
                .totalReviews(0)
                .build();

        return repository.save(vendor);
    }

    @Override
    public List<VendorResponse> findAll() {
        Sort sort = Sort.by(
                Sort.Order.desc("createdAt"),
                Sort.Order.asc("isApproved")
        );
        return repository.findAll(sort).stream()
                .map(this::toResponse)
                .toList();
    }


    public List<TopVendorResponse> getTopVendors(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = repository.findTopVendorsWithStats(pageable);

        return results.stream()
                .map(this::mapToTopVendorDto)
                .collect(Collectors.toList());
    }

    private TopVendorResponse mapToTopVendorDto(Object[] result) {
        Vendor vendor = (Vendor) result[0];
        Long orderCount = (Long) result[1];
        Double completionRate = (Double) result[2];

        return TopVendorResponse.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .logoUrl(vendor.getLogoUrl())
                .cuisineType(vendor.getCuisineType())
                .rating(vendor.getRating())
                .totalReviews(vendor.getTotalReviews())
                .totalOrders(orderCount != null ? orderCount : 0L)
                .completionRate(completionRate != null ? completionRate : 0.0)
                .deliveryFee(vendor.getDeliveryFee())
                .isOpen(vendor.isOpen())
                .address(vendor.getAddress())
                .build();
    }

    // Alternative method using simple approach
    public List<TopVendorResponse> getTopVendorsSimple(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Vendor> vendors = repository.findTopVendorsByOrderCount(pageable);

        return vendors.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private TopVendorResponse convertToDto(Vendor vendor) {
        // Calculate order statistics
        long totalOrders = vendor.getOrders().size();
        long deliveredOrders = vendor.getOrders().stream()
                .mapToLong(order -> order.getOrderStatus() == OrderStatus.SUCCESS ? 1 : 0)
                .sum();

        double completionRate = totalOrders > 0 ?
                (double) deliveredOrders / totalOrders * 100 : 0.0;

        return TopVendorResponse.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .logoUrl(vendor.getLogoUrl())
                .cuisineType(vendor.getCuisineType())
                .rating(vendor.getRating())
                .totalReviews(vendor.getTotalReviews())
                .totalOrders(totalOrders)
                .completionRate(Math.round(completionRate * 10.0) / 10.0)
                .deliveryFee(vendor.getDeliveryFee())
                .isOpen(vendor.isOpen())
                .address(vendor.getAddress())
                .build();
    }
}
