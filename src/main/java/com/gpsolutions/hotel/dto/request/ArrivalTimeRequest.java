package com.gpsolutions.hotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ArrivalTimeRequest(
        @NotBlank(message = "Check-in time is required")
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Check-in time must be in HH:mm format")
        String checkIn,

        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Check-out time must be in HH:mm format")
        String checkOut
) {
}