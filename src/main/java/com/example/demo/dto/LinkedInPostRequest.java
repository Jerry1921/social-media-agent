package com.example.demo.dto;

public class LinkedInPostRequest {

    private String content;

    public LinkedInPostRequest() {
    }

    public LinkedInPostRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}