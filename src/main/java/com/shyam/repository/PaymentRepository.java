package com.shyam.repository;

import com.shyam.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository
        extends JpaRepository<Payment, Long>,
                JpaSpecificationExecutor<Payment> {

    Payment findByRazorpayOrderId(String razorpayOrderId);
}