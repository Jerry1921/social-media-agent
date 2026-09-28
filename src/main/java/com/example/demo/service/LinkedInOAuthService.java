package com.example.demo.service;

import com.example.demo.entity.LinkedInConnection;
import com.example.demo.entity.OAuthState;
import com.example.demo.repository.LinkedInConnectionRepository;
import com.example.demo.repository.OAuthStateRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.dto.LinkedInTokenResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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

    @Value("${linkedin.client-secret}")
    private String clientSecret;

    private final OAuthStateRepository oauthStateRepository;

    //private final OAuthStateRepository oauthStateRepository;
    private final LinkedInConnectionRepository linkedInConnectionRepository;

    public LinkedInOAuthService(
            OAuthStateRepository oauthStateRepository,
            LinkedInConnectionRepository linkedInConnectionRepository
    ) {
        this.oauthStateRepository = oauthStateRepository;
        this.linkedInConnectionRepository = linkedInConnectionRepository;
    }

    public void saveLinkedInConnection(
            String userEmail,
            LinkedInTokenResponse tokenResponse
    ) {

        LocalDateTime expiresAt =
                LocalDateTime.now()
                        .plusSeconds(tokenResponse.getExpiresIn());

        LinkedInConnection connection =
                linkedInConnectionRepository
                        .findByUserEmail(userEmail)
                        .orElse(
                                LinkedInConnection.builder()
                                        .userEmail(userEmail)
                                        .createdAt(LocalDateTime.now())
                                        .build()
                        );

        connection.setAccessToken(tokenResponse.getAccessToken());
        connection.setExpiresAt(expiresAt);
        connection.setScope(tokenResponse.getScope());
        connection.setUpdatedAt(LocalDateTime.now());

        linkedInConnectionRepository.save(connection);
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

    public LinkedInTokenResponse exchangeCodeForToken(String code) {

        String tokenUrl =
                "https://www.linkedin.com/oauth/v2/accessToken";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.APPLICATION_FORM_URLENCODED
        );

        MultiValueMap<String, String> body =
                new LinkedMultiValueMap<>();

        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        return restTemplate.postForObject(
                tokenUrl,
                request,
                LinkedInTokenResponse.class
        );
    }
}