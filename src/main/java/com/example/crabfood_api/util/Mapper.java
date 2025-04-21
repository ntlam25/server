package com.example.crabfood_api.util;

import com.example.crabfood_api.dto.response.AddressResponse;
import com.example.crabfood_api.dto.response.UserResponse;
import com.example.crabfood_api.dto.response.VendorResponse;
import com.example.crabfood_api.model.entity.Address;
import com.example.crabfood_api.model.entity.User;
import com.example.crabfood_api.model.entity.Vendor;

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
        response.setRestaurantName(domainEntity.getRestaurantName());
        response.setDescription(domainEntity.getDescription());
        response.setLogoUrl(domainEntity.getLogoUrl());
        response.setCoverImageUrl(domainEntity.getCoverImageUrl());
        response.setCuisineType(domainEntity.getCuisineType());
        response.setAddress(domainEntity.getAddress());
        response.setLongitude(LocationHelper.getLongitude(domainEntity.getLocation()));
        response.setLatitude(LocationHelper.getLatitude(domainEntity.getLocation()));
        response.setServiceRadiusKm(domainEntity.getServiceRadiusKm());
        response.setOpeningTime(domainEntity.getOpeningTime());
        response.setClosingTime(domainEntity.getClosingTime());
        response.setApproved(domainEntity.isApproved());
        response.setRating(domainEntity.getRating());
        response.setTotalReviews(domainEntity.getTotalReviews());
        response.setMinOrderAmount(domainEntity.getMinOrderAmount());
        response.setDeliveryFee(domainEntity.getDeliveryFee());
        response.setOpen(domainEntity.isOpen());
        // response.setCategories(domainEntity.getCategories());
        // response.setCoupons(domainEntity.getCoupons());
        // response.setOrders(domainEntity.getOrders());
        // response.setReviews(domainEntity.getReviews());
        return response;
    }

    public static AddressResponse toAddressResponse(Address domainEntity) {
        String fullAddress = String.format("%s, %s, %s, %s",
                domainEntity.getAddressLine(),
                domainEntity.getWard(),
                domainEntity.getDistrict(),
                domainEntity.getCity());

        return AddressResponse.builder()
                .id(domainEntity.getId())
                .fullAddress(fullAddress)
                .city(domainEntity.getCity())
                .district(domainEntity.getDistrict())
                .ward(domainEntity.getWard())
                .latitude(domainEntity.getLatitude())
                .longitude(domainEntity.getLongitude())
                .isDefault(domainEntity.getIsDefault())
                .recipientName(domainEntity.getRecipientName())
                .recipientPhone(domainEntity.getRecipientPhone())
                .build();
    }
}
