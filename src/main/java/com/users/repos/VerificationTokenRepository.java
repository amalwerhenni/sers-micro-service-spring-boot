package com.users.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.users.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
}
