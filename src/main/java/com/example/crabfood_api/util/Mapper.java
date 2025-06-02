package com.example.crabfood_api.util;

import com.example.crabfood_api.dto.request.FoodOptionRequest;
import com.example.crabfood_api.dto.request.FoodRequest;
import com.example.crabfood_api.dto.request.OptionChoiceRequest;
import com.example.crabfood_api.dto.response.*;
import com.example.crabfood_api.model.entity.*;

import java.time.LocalDateTime;
import java.util.List;

public class Mapper {

    private Mapper() {
    }

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .roles(user.getRoles()
                        .stream()
                        .map(role -> role.getRole().name())
                        .toArray(String[]::new))
                .emailVerified(user.isEmailVerified())
                .phoneVerified(user.isPhoneVerified())
                .isActive(user.isActive())
                .lastLogin(user.getLastLogin())
                .build();
    }

    public static VendorResponse toVendorResponse(Vendor domainEntity) {
        VendorResponse response = new VendorResponse();
        response.setId(domainEntity.getId());
        response.setName(domainEntity.getName());
        response.setDescription(domainEntity.getDescription());
        response.setLogoUrl(domainEntity.getLogoUrl());
        response.setCoverImageUrl(domainEntity.getCoverImageUrl());
        response.setAddress(domainEntity.getAddress());
        response.setLongitude(LocationHelper.getLongitude(domainEntity.getLocation()));
        response.setLatitude(LocationHelper.getLatitude(domainEntity.getLocation()));
        response.setServiceRadiusKm(domainEntity.getServiceRadiusKm());
        response.setOpeningTime(domainEntity.getOpeningTime());
        response.setClosingTime(domainEntity.getClosingTime());
        response.setApproved(domainEntity.isApproved());
        response.setRating(domainEntity.getRating());
        response.setTotalReviews(domainEntity.getTotalReviews());
        response.setDeliveryFee(domainEntity.getDeliveryFee());
        response.setOpen(domainEntity.isOpen());
        response.setCuisineType(domainEntity.getCuisineType());
        response.setUserId(domainEntity.getUser().getId());
        response.setActive(domainEntity.isActive());
        return response;
    }

    public static AddressResponse toAddressResponse(Address domainEntity) {

        return AddressResponse.builder()
                .id(domainEntity.getId())
                .label(domainEntity.getLabel())
                .fullAddress(domainEntity.getFullAddress())
                .latitude(domainEntity.getLatitude())
                .longitude(domainEntity.getLongitude())
                .isDefault(domainEntity.getIsDefault())
                .build();
    }

    public static CategoryResponse toCategoryResponse(Category domainEntity) {
        return CategoryResponse.builder()
                .id(domainEntity.getId())
                .name(domainEntity.getName())
                .description(domainEntity.getDescription())
                .imageUrl(domainEntity.getImageUrl())
                .slug(domainEntity.getSlug())
                .displayOrder(domainEntity.getDisplayOrder())
                .isActive(domainEntity.isActive())
                .isGlobal(domainEntity.isGlobal())
                .vendorId(domainEntity.getVendor().getId())
                .vendorName(domainEntity.getVendor().getName())
                .foods(domainEntity.getFoods().stream()
                        .map(Mapper::toFoodResponse)
                        .toList())
                .build();
    }

    public static SimpleCategoryResponse toSimpleCategoryResponse(Category category) {
        return SimpleCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static FoodResponse toFoodResponse(Food domainEntity) {
        return FoodResponse.builder()
                .id(domainEntity.getId())
                .name(domainEntity.getName())
                .description(domainEntity.getDescription())
                .imageUrl(domainEntity.getImageUrl())
                .price(domainEntity.getPrice())
                .isAvailable(domainEntity.isAvailable())
                .preparationTime(domainEntity.getPreparationTime())
                .rating(domainEntity.getRating())
                .isFeatured(domainEntity.isFeatured())
                .createdAt(domainEntity.getCreatedAt())
                .vendorName(domainEntity.getVendor().getName())
                .updatedAt(domainEntity.getUpdatedAt())
                .categories(domainEntity.getCategories().stream()
                        .map(Mapper::toSimpleCategoryResponse)
                        .toList())
                .vendorId(domainEntity.getVendor().getId())
                .options(domainEntity.getOptions().stream()
                        .map(Mapper::toOptionsResponse)
                        .toList())
                .build();
    }

    public static FoodOptionResponse toOptionsResponse(FoodOption options) {
        return FoodOptionResponse.builder()
                .optionId(options.getId())
                .name(options.getName())
                .isRequired(options.isRequired())
                .minSelection(options.getMinSelection())
                .maxSelection(options.getMaxSelection())
                .choices(options.getChoices().stream()
                        .map(Mapper::toOptionChoiceResponse)
                        .toList())
                .build();
    }

    public static OptionChoiceResponse toOptionChoiceResponse(OptionChoice choice) {
        return OptionChoiceResponse.builder()
                .id(choice.getId())
                .name(choice.getName())
                .priceAdjustment(choice.getPriceAdjustment())
                .isDefault(choice.isDefault())
                .build();
    }

    public static OrderResponse toOrderResponse(Order order) {
        // Chuyển đổi Order sang OrderDTO
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .orderNumber(order.getOrderNumber())
                .vendorResponse(Mapper.toVendorResponse(order.getVendor()))
                .deliveryAddressText(order.getDeliveryAddressText())
                .deliveryAddressId(order.getDeliveryAddress().getId())
                .deliveryLatitude(order.getDeliveryLatitude())
                .deliveryLongitude(order.getDeliveryLongitude())
                .deliveryNotes(order.getDeliveryNotes())
                .subtotal(order.getSubtotal())
                .paymentMethod(order.getPaymentMethod().name())
                .deliveryFee(order.getDeliveryFee())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .orderDateTime(order.getCreatedAt())
                .recipientName(order.getRecipientName())
                .recipientPhone(order.getRecipientPhone())
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                .actualDeliveryTime(order.getActualDeliveryTime())
                .items(order.getOrderFoods()
                        .stream()
                        .map(Mapper::toOrderFoodResponse)
                        .toList())
                .build();
    }

    public static OrderFoodResponse toOrderFoodResponse(OrderFood orderFood) {
        return OrderFoodResponse.builder()
                .id(orderFood.getId())
                .foodId(orderFood.getFood().getId())
                .foodName(orderFood.getFoodName())
                .quantity(orderFood.getQuantity())
                .foodImageUrl(orderFood.getFood().getImageUrl())
                .foodPrice(orderFood.getFoodPrice())
                .choices(orderFood.getChoices()
                        .stream()
                        .map(Mapper::toOrderFoodChoiceResponse)
                        .toList())
                .build();
    }

    public static OrderFoodChoiceResponse toOrderFoodChoiceResponse(OrderFoodChoice choice) {
        return OrderFoodChoiceResponse.builder()
                .id(choice.getId())
                .optionName(choice.getOptionName())
                .choiceName(choice.getChoiceName())
                .priceAdjustment(choice.getPriceAdjustment())
                .optionId(choice.getOptionId())
                .build();
    }


    public static OptionChoice toOptionChoiceEntity(OptionChoiceRequest request) {
        return OptionChoice.builder()
                .name(request.getName())
                .priceAdjustment(request.getPriceAdjustment())
                .isDefault(request.isDefault())
                .build();
    }

    public static OptionChoice toOptionChoiceEntity(OptionChoiceRequest request, FoodOption option) {
        return OptionChoice.builder()
                .option(option)
                .name(request.getName())
                .priceAdjustment(request.getPriceAdjustment())
                .isDefault(request.isDefault())
                .build();
    }

    public static FoodOption toFoodOptionEntity(FoodOptionRequest request, Food food) {
        FoodOption option = FoodOption.builder()
                .food(food)
                .name(request.getName())
                .isRequired(request.isRequired())
                .minSelection(request.getMinSelection())
                .maxSelection(request.getMaxSelection())
                .build();

        List<OptionChoice> choices = request.getChoices().stream()
                .map(choiceRequest -> {
                    OptionChoice choice =  Mapper.toOptionChoiceEntity(choiceRequest);
                    choice.setOption(option);
                    return choice;
                })
                .toList();

        option.setChoices(choices);
        return option;
    }
    public static Food toFoodEntity(FoodRequest request, Vendor vendor) {
        Food food = Food.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .isAvailable(request.isAvailable())
                .isFeatured(request.isFeatured())
                .createdAt(LocalDateTime.now())
                .preparationTime(request.getPreparationTime())
                .vendor(vendor)
                .build();

        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            List<FoodOption> foodOptions = request.getOptions().stream()
                    .map(optReq -> Mapper.toFoodOptionEntity(optReq, food))
                    .toList();
            food.setOptions(foodOptions);
        }

        return food;
    }
}
