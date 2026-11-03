package com.example.demo.repository;

import com.example.demo.entity.SocialPost;
import com.example.demo.entity.SocialPostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SocialPostRepository
        extends JpaRepository<SocialPost, Long> {

    List<SocialPost> findByUserEmail(String userEmail);

    List<SocialPost> findByUserEmailAndStatus(
            String userEmail,
            SocialPostStatus status
    );

    Optional<SocialPost> findByIdAndUserEmail(
            Long id,
            String userEmail
    );
}