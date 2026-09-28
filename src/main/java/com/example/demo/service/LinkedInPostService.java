package com.example.demo.service;

import com.example.demo.entity.LinkedInConnection;
import com.example.demo.repository.LinkedInConnectionRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class LinkedInPostService {

    private final LinkedInConnectionRepository linkedInConnectionRepository;

    public LinkedInPostService(
            LinkedInConnectionRepository linkedInConnectionRepository
    ) {
        this.linkedInConnectionRepository = linkedInConnectionRepository;
    }

    public String createPost(
            String userEmail,
            String content
    ) {

        // 1. Find LinkedIn connection
        LinkedInConnection connection =
                linkedInConnectionRepository
                        .findByUserEmail(userEmail)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "LinkedIn account is not connected"
                                )
                        );

        // 2. Get access token
        String accessToken = connection.getAccessToken();

        // 3. Get LinkedIn member ID
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);

        HttpEntity<Void> userInfoRequest =
                new HttpEntity<>(userInfoHeaders);

        ResponseEntity<Map> userInfoResponse =
                restTemplate.exchange(
                        "https://api.linkedin.com/v2/userinfo",
                        HttpMethod.GET,
                        userInfoRequest,
                        Map.class
                );

        Map userInfo = userInfoResponse.getBody();

        if (userInfo == null || userInfo.get("sub") == null) {
            throw new RuntimeException(
                    "Could not retrieve LinkedIn member ID"
            );
        }

        String memberId = userInfo.get("sub").toString();

        // 4. Create author
        String author = "urn:li:person:" + memberId;

        // 5. Create LinkedIn post body
        Map<String, Object> post = new HashMap<>();

        post.put("author", author);
        post.put("commentary", content);
        post.put("visibility", "PUBLIC");
        post.put("distribution", Map.of(
                "feedDistribution", "MAIN_FEED",
                "targetEntities", new String[]{},
                "thirdPartyDistributionChannels", new String[]{}
        ));
        post.put("lifecycleState", "PUBLISHED");
        post.put("isReshareDisabledByAuthor", false);

        // 6. Headers
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // LinkedIn API version
        headers.set(
                "LinkedIn-Version",
                "202601"
        );

        headers.set(
                "X-Restli-Protocol-Version",
                "2.0.0"
        );

        // 7. Create request
        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(post, headers);

        // 8. Send post to LinkedIn
        ResponseEntity<String> response =
                restTemplate.exchange(
                        "https://api.linkedin.com/rest/posts",
                        HttpMethod.POST,
                        request,
                        String.class
                );

        // 9. Return LinkedIn response
        return response.getBody();
    }
}