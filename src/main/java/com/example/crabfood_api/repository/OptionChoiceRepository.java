package com.example.crabfood_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.crabfood_api.model.entity.OptionChoice;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionChoiceRepository extends JpaRepository<OptionChoice, Long> {
}
