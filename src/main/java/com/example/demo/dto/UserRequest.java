package com.example.demo.dto;

import com.example.demo.entity.AuthenticationProvider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

    private String fullName;

    private String email;

    private String password;

    private AuthenticationProvider provider;
}