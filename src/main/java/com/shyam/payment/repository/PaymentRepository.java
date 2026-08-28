package com.shyam.payment.repository;

import com.shyam.common.constants.PaymentStatus;
import com.shyam.payment.entity.Payment;
import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

  Optional<Payment> findByPaymentReference(String paymentReference);

  Optional<Payment> findByIdempotencyKey(String idempotencyKey);

  Optional<Payment> findTopByOrder_IdOrderByCreatedAtDesc(Long orderId);

  List<Payment> findByOrder_IdOrderByCreatedAtDesc(Long orderId);

  Optional<Payment> findFirstByOrder_IdAndStatusInOrderByCreatedAtDesc(
      Long orderId, Collection<PaymentStatus> statuses);

  boolean existsByOrder_IdAndStatus(Long orderId, PaymentStatus status);

  boolean existsByGatewayPaymentId(String gatewayPaymentId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT p FROM Payment p JOIN FETCH p.order WHERE p.id = :paymentId")
  Optional<Payment> findByIdForUpdate(@Param("paymentId") Long paymentId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT p FROM Payment p JOIN FETCH p.order WHERE p.paymentReference = :paymentReference")
  Optional<Payment> findByPaymentReferenceForUpdate(
      @Param("paymentReference") String paymentReference);
}
