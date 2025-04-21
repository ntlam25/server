package com.example.crabfood_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.crabfood_api.model.entity.User;
import com.example.crabfood_api.model.enums.Role;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByRoles(Set<Role> roles);
    
    List<User> findByIsActive(boolean isActive);
    
    Optional<User> findByPhone(String phone);
    
    boolean existsByPhone(String phone);
    
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);

    Optional<User> findByEmailOrUsername(String email, String username);
}