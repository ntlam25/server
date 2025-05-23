package com.example.crabfood_api.repository;

import com.example.crabfood_api.model.entity.RiderProfile;
import com.example.crabfood_api.model.entity.User;
import com.example.crabfood_api.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface RiderProfileRepository extends JpaRepository<RiderProfile, Long> {
}