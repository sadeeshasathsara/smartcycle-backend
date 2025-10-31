package com.smartcycle.smartcycleapplication.controllers;

import com.smartcycle.smartcycleapplication.dtos.LoginRequestDTO;
import com.smartcycle.smartcycleapplication.dtos.RegistrationRequestDTO;
import com.smartcycle.smartcycleapplication.dtos.UserResponseDTO;
import com.smartcycle.smartcycleapplication.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint for new user registration.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegistrationRequestDTO request) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserResponseDTO newUser = authService.registerUser(request);
            response.put("success", true);
            response.put("message", "Account successfully created");
            response.put("data", newUser);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An unexpected error occurred during registration.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint for user login.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody LoginRequestDTO request,
            HttpServletResponse servletResponse) {

        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> authResult = authService.loginUser(request, servletResponse);

            response.put("success", true);
            response.put("message", "Logged in successfully");
            response.put("data", authResult.get("user"));
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            response.put("success", false);
            response.put("message", "Invalid email or password");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An unexpected error occurred during login.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}