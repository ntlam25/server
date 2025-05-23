package com.example.crabfood_api.service.cart;

import com.example.crabfood_api.dto.request.CartItemDTO;
import com.example.crabfood_api.dto.request.OptionChoiceDTO;
import com.example.crabfood_api.model.entity.CartItem;
import com.example.crabfood_api.repository.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartService implements ICartService {
    private final CartRepository cartRepository;

    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public List<CartItemDTO> getCartByUserId(Long userId) {
        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        return cartItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void syncCart(Long userId, List<CartItemDTO> clientCart) {
        // Nếu giỏ hàng client trống, xóa giỏ hàng hiện tại
        if (clientCart == null || clientCart.isEmpty()) {
            cartRepository.deleteByUserId(userId);
            return;
        }

        // Lấy giỏ hàng hiện tại từ DB
        List<CartItem> serverCart = cartRepository.findByUserId(userId);

        // Map chứa các mục giỏ hàng đã xử lý/cập nhật
        Map<String, CartItem> processedItems = new HashMap<>();

        // Duyệt qua từng item trong giỏ hàng client
        for (CartItemDTO clientItem : clientCart) {
            // Tạo key duy nhất cho mỗi món ăn + tùy chọn
            String itemKey = generateItemKey(clientItem);
            boolean itemExistsOnServer = false;

            // Kiểm tra xem item đã tồn tại trong giỏ hàng server chưa
            for (CartItem serverItem : serverCart) {
                String serverItemKey = generateItemKey(convertToDTO(serverItem));

                // Nếu item tồn tại trên server
                if (serverItemKey.equals(itemKey)) {
                    itemExistsOnServer = true;

                    // So sánh thời gian cập nhật để xác định phiên bản mới nhất
                    if (clientItem.getLastUpdated() != null && serverItem.getLastUpdated() != null) {
                        if (clientItem.getLastUpdated().after(serverItem.getLastUpdated())) {
                            // Client mới hơn, cập nhật server
                            serverItem.setQuantity(clientItem.getQuantity());
                            serverItem.setLastUpdated(new Date());
                            processedItems.put(itemKey, serverItem);
                        } else {
                            // Server mới hơn, giữ nguyên
                            processedItems.put(itemKey, serverItem);
                        }
                    } else {
                        // Không có thông tin thời gian cập nhật, lấy số lượng lớn hơn
                        if (clientItem.getQuantity() > serverItem.getQuantity()) {
                            serverItem.setQuantity(clientItem.getQuantity());
                        }
                        serverItem.setLastUpdated(new Date());
                        processedItems.put(itemKey, serverItem);
                    }
                    break;
                }
            }

            // Nếu item không tồn tại trên server, thêm mới
            if (!itemExistsOnServer) {
                CartItem newItem = convertToEntity(clientItem);
                newItem.setUserId(userId);
                newItem.setLastUpdated(new Date());
                processedItems.put(itemKey, newItem);
            }
        }

        // Xóa giỏ hàng hiện tại
        cartRepository.deleteByUserId(userId);

        // Lưu tất cả các mục đã xử lý
        for (CartItem item : processedItems.values()) {
            cartRepository.save(item);
        }
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartRepository.deleteByUserId(userId);
    }

    private CartItemDTO convertToDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setFoodId(cartItem.getFoodId());
        dto.setFoodName(cartItem.getFoodName());
        dto.setImageUrl(cartItem.getImageUrl());
        dto.setPrice(cartItem.getPrice());
        dto.setQuantity(cartItem.getQuantity());
        dto.setVendorId(cartItem.getVendorId());
        dto.setSelectedOptions(cartItem.getSelectedOptions());
        dto.setLastUpdated(cartItem.getLastUpdated());
        dto.setSynced(true);
        return dto;
    }

    private CartItem convertToEntity(CartItemDTO dto) {
        CartItem entity = new CartItem();
        entity.setFoodId(dto.getFoodId());
        entity.setFoodName(dto.getFoodName());
        entity.setImageUrl(dto.getImageUrl());
        entity.setPrice(dto.getPrice());
        entity.setQuantity(dto.getQuantity());
        entity.setVendorId(dto.getVendorId());
        entity.setSelectedOptions(dto.getSelectedOptions());
        entity.setLastUpdated(dto.getLastUpdated() != null ? dto.getLastUpdated() : new Date());
        return entity;
    }

    /**
     * Tạo key duy nhất cho mỗi món ăn và tùy chọn để so sánh
     */
    private String generateItemKey(CartItemDTO item) {
        StringBuilder key = new StringBuilder();
        key.append(item.getFoodId());

        if (item.getSelectedOptions() != null && !item.getSelectedOptions().isEmpty()) {
            // Sắp xếp tùy chọn để đảm bảo so sánh nhất quán
            List<OptionChoiceDTO> sortedOptions = item.getSelectedOptions().stream()
                    .sorted((o1, o2) -> {
                        if (o1.getOptionId().equals(o2.getOptionId())) {
                            return o1.getId().compareTo(o2.getId());
                        }
                        return o1.getOptionId().compareTo(o2.getOptionId());
                    })
                    .collect(Collectors.toList());

            for (OptionChoiceDTO option : sortedOptions) {
                key.append("_").append(option.getOptionId()).append("-").append(option.getId());
            }
        }

        return key.toString();
    }

    public static List<OptionChoiceDTO> flattenOptionChoices(Map<Long, List<OptionChoiceDTO>> map) {
        if (map == null) return Collections.emptyList();

        return map.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}