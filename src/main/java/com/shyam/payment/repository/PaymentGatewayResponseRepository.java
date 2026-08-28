package com.shyam.payment.repository;

import com.shyam.payment.entity.PaymentGatewayResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentGatewayResponseRepository
    extends JpaRepository<PaymentGatewayResponse, Long> {}
