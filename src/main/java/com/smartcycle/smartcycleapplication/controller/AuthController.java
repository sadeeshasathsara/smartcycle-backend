package com.smartcycle.smartcycleapplication.controller;

import com.smartcycle.smartcycleapplication.config.JwtUtil;
import com.smartcycle.smartcycleapplication.dto.AuthRequest;
import com.smartcycle.smartcycleapplication.dto.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        // Simple demo authentication - in real app, validate against database
        if ("demo@smartcycle.com".equals(authRequest.getEmail()) && "password123".equals(authRequest.getPassword())) {
            String token = jwtUtil.generateToken(authRequest.getEmail());
            return ResponseEntity.ok(new AuthResponse(token, "Bearer"));
        }
        return ResponseEntity.badRequest().build();
    }
}
