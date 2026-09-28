package com.example.demo.repository;

import com.example.demo.entity.LinkedInConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LinkedInConnectionRepository
        extends JpaRepository<LinkedInConnection, Long> {

    Optional<LinkedInConnection> findByUserEmail(String userEmail);
}