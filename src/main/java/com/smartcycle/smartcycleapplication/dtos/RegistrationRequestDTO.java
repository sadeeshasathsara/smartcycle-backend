package com.smartcycle.smartcycleapplication.dtos;

import lombok.Data;

@Data
public class RegistrationRequestDTO {
    private String email;
    private String password;
    private String name;
    private String role;
    private String address; // Optional, only used for residents
}
