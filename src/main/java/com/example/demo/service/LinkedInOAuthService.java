package com.example.demo.service;

import com.example.demo.entity.OAuthState;
import com.example.demo.repository.OAuthStateRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LinkedInOAuthService {

    @Value("${linkedin.client-id}")
    private String clientId;

    @Value("${linkedin.redirect-uri}")
    private String redirectUri;

    private final OAuthStateRepository oauthStateRepository;

    public LinkedInOAuthService(
            OAuthStateRepository oauthStateRepository
    ) {
        this.oauthStateRepository = oauthStateRepository;
    }

    public String createAuthorizationUrl(String userEmail) {

        String state = UUID.randomUUID().toString();

        OAuthState oauthState = OAuthState.builder()
                .state(state)
                .userEmail(userEmail)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        oauthStateRepository.save(oauthState);

        String scope = "openid profile email w_member_social";

        return "https://www.linkedin.com/oauth/v2/authorization"
                + "?response_type=code"
                + "&client_id=" + encode(clientId)
                + "&redirect_uri=" + encode(redirectUri)
                + "&state=" + encode(state)
                + "&scope=" + encode(scope);
    }

    private String encode(String value) {
        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8
        );
    }
}