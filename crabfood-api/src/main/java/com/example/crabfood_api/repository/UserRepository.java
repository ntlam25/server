package com.example.crabfood_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.crabfood_api.model.entity.User;
import com.example.crabfood_api.model.enums.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(Role role);
    
    List<User> findByIsActive(boolean isActive);
    
    Optional<User> findByPhone(String phone);
    
    boolean existsByPhone(String phone);
}