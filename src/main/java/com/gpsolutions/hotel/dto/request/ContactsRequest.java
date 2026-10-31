package com.gpsolutions.hotel.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ContactsRequest(
    @NotBlank(message = "Phone number is required") String phone,
    @NotBlank(message = "Email is required") @Email(message = "Email must be valid")
        String email) {}
