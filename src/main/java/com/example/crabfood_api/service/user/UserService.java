package com.example.crabfood_api.service.user;

import com.example.crabfood_api.dto.request.UserRequest;
import com.example.crabfood_api.dto.response.UserResponse;
import com.example.crabfood_api.exception.ResourceNotFoundException;
import com.example.crabfood_api.model.entity.User;
import com.example.crabfood_api.repository.UserRepository;
import com.example.crabfood_api.service.AbstractCrudService;
import com.example.crabfood_api.util.Mapper;
import org.springframework.stereotype.Service;

@Service
public class UserService extends AbstractCrudService<UserRequest, UserResponse, UserRepository, User, Long>
        implements IUserService {
    protected UserService(UserRepository repository) {
        super(repository, User.class);
    }

    @Override
    protected User createAndSave(UserRequest request) {
        return null;
    }

    @Override
    protected User updateAndSave(Long id, UserRequest request) {
        User user = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User with id " + id + " not found"));
        user.setPhone(request.getPhone());
        user.setFullName(request.getFullName());
        user.setPasswordHash(request.getPassword());
        user.setAvatarUrl(request.getAvatarUrl());

        return repository.save(user);
    }

    @Override
    protected UserResponse toResponse(User domainEntity) {
        return Mapper.toUserResponse(domainEntity);
    }
}
