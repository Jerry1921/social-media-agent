package com.example.demo.repository;

import com.example.demo.entity.OAuthState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OAuthStateRepository
        extends JpaRepository<OAuthState, Long> {

    Optional<OAuthState> findByState(String state);

    @Modifying
    @Query("DELETE FROM OAuthState o WHERE o.state = :state")
    void deleteByState(String state);
}