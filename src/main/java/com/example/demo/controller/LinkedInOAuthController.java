package com.example.demo.controller;

import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.OAuthState;
import com.example.demo.repository.OAuthStateRepository;
import com.example.demo.service.LinkedInOAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/oauth/linkedin")
public class LinkedInOAuthController {

    private final LinkedInOAuthService linkedInOAuthService;
    private final OAuthStateRepository oauthStateRepository;

    public LinkedInOAuthController(
            LinkedInOAuthService linkedInOAuthService,
            OAuthStateRepository oauthStateRepository
    ) {
        this.linkedInOAuthService = linkedInOAuthService;
        this.oauthStateRepository = oauthStateRepository;
    }

    @GetMapping("/authorize")
    public ResponseEntity<String> authorize(
            Authentication authentication
    ) {

        String userEmail = authentication.getName();

        String authorizationUrl =
                linkedInOAuthService.createAuthorizationUrl(userEmail);

        return ResponseEntity.ok(authorizationUrl);
    }

    @Transactional
    @GetMapping("/callback")
    public ResponseEntity<String> callback(
            @RequestParam String code,
            @RequestParam String state
    ) {

        OAuthState oauthState =
                oauthStateRepository.findByState(state)
                        .orElse(null);

        if (oauthState == null) {
            return ResponseEntity
                    .status(401)
                    .body("Invalid OAuth state");
        }

        if (oauthState.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            oauthStateRepository.deleteByState(state);

            return ResponseEntity
                    .status(401)
                    .body("OAuth state has expired");
        }

        String userEmail = oauthState.getUserEmail();

        // State can only be used once
        oauthStateRepository.deleteByState(state);

        return ResponseEntity.ok(
                "OAuth callback received for: " + userEmail
        );
    }
}