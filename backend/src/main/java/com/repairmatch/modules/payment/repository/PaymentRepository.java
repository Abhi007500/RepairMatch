package com.repairmatch.modules.payment.repository;

import com.repairmatch.modules.payment.domain.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    List<Payment> findByBookingIdOrderByCreatedAtDesc(String bookingId);

    default List<Payment> findByBookingId(String bookingId) {
        return findByBookingIdOrderByCreatedAtDesc(bookingId);
    }

    Optional<Payment> findByProviderOrderId(String providerOrderId);

    Optional<Payment> findByProviderPaymentId(String providerPaymentId);
}
