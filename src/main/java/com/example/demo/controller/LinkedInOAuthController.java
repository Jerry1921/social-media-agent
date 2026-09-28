package com.example.demo.controller;

import com.example.demo.dto.LinkedInTokenResponse;
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

        // 1. Find the OAuth state
        OAuthState oauthState =
                oauthStateRepository.findByState(state)
                        .orElse(null);

        // 2. Validate state
        if (oauthState == null) {
            return ResponseEntity
                    .status(401)
                    .body("Invalid OAuth state");
        }

        // 3. Check expiration
        if (oauthState.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            oauthStateRepository.deleteByState(state);

            return ResponseEntity
                    .status(401)
                    .body("OAuth state has expired");
        }

        // 4. Get the user who started the OAuth process
        String userEmail = oauthState.getUserEmail();

        // 5. State can only be used once
        oauthStateRepository.deleteByState(state);

        // 6. Exchange authorization code for LinkedIn access token
        LinkedInTokenResponse tokenResponse =
                linkedInOAuthService.exchangeCodeForToken(code);

        // 7. Don't return the token to the browser
        return ResponseEntity.ok(
                "LinkedIn connected successfully for: " + userEmail
        );
    }
    }
