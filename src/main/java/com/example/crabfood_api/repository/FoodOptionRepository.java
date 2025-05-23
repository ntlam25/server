package com.example.crabfood_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.crabfood_api.model.entity.FoodOption;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodOptionRepository extends JpaRepository<FoodOption, Long> {
}
