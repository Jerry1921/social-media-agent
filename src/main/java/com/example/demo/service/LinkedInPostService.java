package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

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
    // this is for initializing the image upload
    public Map<String, String> initializeImageUpload(
            String accessToken,
            String memberId
    ) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> initializeRequest = Map.of(
                "initializeUploadRequest",
                Map.of(
                        "owner", "urn:li:person:" + memberId
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("LinkedIn-Version", "202601");
        headers.set("X-Restli-Protocol-Version", "2.0.0");

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(initializeRequest, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.linkedin.com/rest/images?action=initializeUpload",
                HttpMethod.POST,
                request,
                Map.class
        );

        Map body = response.getBody();

        if (body == null || !(body.get("value") instanceof Map)) {
            throw new RuntimeException(
                    "LinkedIn did not return image upload details"
            );
        }

        Map value = (Map) body.get("value");

        String uploadUrl = (String) value.get("uploadUrl");
        String imageUrn = (String) value.get("image");

        if (uploadUrl == null || imageUrn == null) {
            throw new RuntimeException(
                    "LinkedIn response is missing the upload URL or image URN"
            );
        }

        return Map.of(
                "uploadUrl", uploadUrl,
                "imageUrn", imageUrn
        );
    }



    public void uploadImage(
            String uploadUrl,
            MultipartFile image
            //The uploaded image file received by Spring Boot application.
    ) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please provide a valid image"
            );
        }

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();

            String contentType = image.getContentType();
            // returns the file's MIME type

            //Use the file's content type if available. Otherwise, use a generic binary content type.
            headers.setContentType(
                    contentType != null
                            ? MediaType.parseMediaType(contentType)
                            : MediaType.APPLICATION_OCTET_STREAM
            );

            HttpEntity<byte[]> request = new HttpEntity<>(
                    image.getBytes(),
                    headers
            );

            restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.PUT,
                    request,
                    Void.class
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to upload image to LinkedIn",
                    e
            );
        }
    }

    public String createImagePost(
            String userEmail,
            String content,
            MultipartFile image
    ) {
        LinkedInConnection connection =
                linkedInConnectionRepository
                        .findByUserEmail(userEmail)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "LinkedIn account is not connected"
                                )
                        );

        String accessToken = connection.getAccessToken();

        // Step 1: Retrieve the LinkedIn member ID
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);

        ResponseEntity<Map> userInfoResponse =
                restTemplate.exchange(
                        "https://api.linkedin.com/v2/userinfo",
                        HttpMethod.GET,
                        new HttpEntity<>(userInfoHeaders),
                        Map.class
                );

        Map userInfo = userInfoResponse.getBody();

        if (userInfo == null || userInfo.get("sub") == null) {
            throw new RuntimeException(
                    "Could not retrieve LinkedIn member ID"
            );
        }

        String memberId = userInfo.get("sub").toString();

        // Step 2: Initialize the image upload
        Map<String, String> uploadDetails =
                initializeImageUpload(accessToken, memberId);

        // Step 3: Upload the actual image
        uploadImage(uploadDetails.get("uploadUrl"), image);

        String imageUrn = uploadDetails.get("imageUrn");

        // Step 4: Create the LinkedIn post
        Map<String, Object> media = Map.of(
                "id", imageUrn
        );

        Map<String, Object> post = new HashMap<>();
        post.put("author", "urn:li:person:" + memberId);
        post.put("commentary", content);
        post.put("visibility", "PUBLIC");
        post.put("distribution", Map.of(
                "feedDistribution", "MAIN_FEED",
                "targetEntities", new String[]{},
                "thirdPartyDistributionChannels", new String[]{}
        ));
        post.put("content", Map.of("media", media));
        post.put("lifecycleState", "PUBLISHED");
        post.put("isReshareDisabledByAuthor", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("LinkedIn-Version", "202601");
        headers.set("X-Restli-Protocol-Version", "2.0.0");

        ResponseEntity<String> response =
                restTemplate.exchange(
                        "https://api.linkedin.com/rest/posts",
                        HttpMethod.POST,
                        new HttpEntity<>(post, headers),
                        String.class
                );

        return response.getBody();
    }
}