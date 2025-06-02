package com.example.crabfood_api.repository;

import com.example.crabfood_api.model.entity.UserRole;
import com.example.crabfood_api.model.entity.UserRoleId;
import com.example.crabfood_api.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository  extends JpaRepository<UserRole, UserRoleId> {
    long countByRole(Role role);
}
