package com.example.crabfood_api.repository;

import com.example.crabfood_api.model.entity.VNPayTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<VNPayTransaction,Long> {

}
