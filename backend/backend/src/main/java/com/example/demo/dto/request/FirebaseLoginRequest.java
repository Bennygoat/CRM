package com.example.demo.dto.request;

import lombok.Data;

@Data
public class FirebaseLoginRequest {
    private String provider;
    private String token;
}
