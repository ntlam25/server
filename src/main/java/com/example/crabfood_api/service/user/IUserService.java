package com.example.crabfood_api.service.user;

import com.example.crabfood_api.dto.request.UserRequest;
import com.example.crabfood_api.dto.response.UserResponse;
import com.example.crabfood_api.model.entity.User;
import com.example.crabfood_api.service.BaseCRUDService;

public interface IUserService extends BaseCRUDService<UserRequest, UserResponse, User, Long> {
    UserResponse updateStatusUser(Long id, boolean status);
}
