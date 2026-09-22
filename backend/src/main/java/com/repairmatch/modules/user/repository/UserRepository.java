package com.repairmatch.modules.user.repository;

import com.repairmatch.modules.user.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("{ '$or': [ { 'phoneNumber': ?0 }, { 'phoneNumber': ?1 }, { 'phoneNumber': { '$regex': ?0, '$options': 'i' } } ] }")
    List<User> findByPhoneMatches(String cleanPhone, String rawPhone);
}
