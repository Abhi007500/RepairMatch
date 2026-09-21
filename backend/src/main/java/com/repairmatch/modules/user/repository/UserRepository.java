package com.repairmatch.modules.user.repository;

import com.repairmatch.modules.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.phoneNumber IS NOT NULL AND (" +
           "REPLACE(REPLACE(REPLACE(REPLACE(u.phoneNumber, ' ', ''), '-', ''), '+91', ''), '+', '') = :cleanPhone " +
           "OR u.phoneNumber = :rawPhone " +
           "OR u.phoneNumber LIKE %:cleanPhone%)")
    List<User> findByPhoneMatches(@Param("cleanPhone") String cleanPhone, @Param("rawPhone") String rawPhone);
}
