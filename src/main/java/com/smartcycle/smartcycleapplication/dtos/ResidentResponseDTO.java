package com.smartcycle.smartcycleapplication.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResidentResponseDTO extends UserResponseDTO {
    private String address;
    private double accountBalance;
}