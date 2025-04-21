package com.example.crabfood_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.crabfood_api.model.entity.Address;
import com.example.crabfood_api.model.entity.User;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
    Optional<Address> findByIdAndUser(Long id, User user);
    List<Address> findByUserAndIsDefault(User user, Boolean isDefault);
    long countByUser(User user);
}