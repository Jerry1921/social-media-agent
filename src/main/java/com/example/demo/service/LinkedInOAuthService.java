package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class LinkedInOAuthService {

    @Value("${linkedin.client-id}")
    private String clientId;

    @Value("${linkedin.redirect-uri}")
    private String redirectUri;

    public String buildAuthorizationUrl(String state) {

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