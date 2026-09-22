package com.repairmatch.modules.auth.repository;

import com.repairmatch.modules.auth.domain.OtpVerification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends MongoRepository<OtpVerification, String> {

    List<OtpVerification> findByPhoneNumberAndVerifiedFalseOrderByCreatedAtDesc(String phoneNumber);

    default List<OtpVerification> findActiveVerifications(String phoneNumber) {
        return findByPhoneNumberAndVerifiedFalseOrderByCreatedAtDesc(phoneNumber);
    }

    default Optional<OtpVerification> findLatestActive(String phoneNumber) {
        List<OtpVerification> list = findActiveVerifications(phoneNumber);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
