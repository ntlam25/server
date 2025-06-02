package com.example.crabfood_api.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterVendorRequest {
    // Thông tin tài khoản
    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50, message = "Username phải từ 3-50 ký tự")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    // Thông tin vendor
    @NotBlank(message = "Tên cửa hàng không được để trống")
    @Size(min = 2, max = 100, message = "Tên cửa hàng phải từ 2-100 ký tự")
    private String vendorName;

    private String description;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @Min(value = 1, message = "Bán kính phục vụ phải ít nhất 1km")
    @Max(value = 50, message = "Bán kính phục vụ không được quá 50km")
    private Double serviceRadiusKm = 5.0;

    private String cuisineType;

    @Min(value = 0, message = "Phí giao hàng không được âm")
    private Double deliveryFee = 0.0;
}
