package com.repairmatch.modules.auth.repository;

import com.repairmatch.modules.auth.domain.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, String> {

    @Query("SELECT o FROM OtpVerification o WHERE o.phoneNumber = :phoneNumber AND o.verified = false ORDER BY o.createdAt DESC")
    List<OtpVerification> findActiveVerifications(@Param("phoneNumber") String phoneNumber);

    default Optional<OtpVerification> findLatestActive(String phoneNumber) {
        List<OtpVerification> list = findActiveVerifications(phoneNumber);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
