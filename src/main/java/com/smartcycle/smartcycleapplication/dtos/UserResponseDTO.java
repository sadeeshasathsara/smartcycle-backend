package com.smartcycle.smartcycleapplication.dtos;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String name;
    private String role;
}