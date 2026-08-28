package com.shyam.payment.repository;

import com.shyam.payment.entity.PaymentTransaction;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

  Optional<PaymentTransaction> findByPayment_IdAndIdempotencyKey(
      Long paymentId, String idempotencyKey);
}
