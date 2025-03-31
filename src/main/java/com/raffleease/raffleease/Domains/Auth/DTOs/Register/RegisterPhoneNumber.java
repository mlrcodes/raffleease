package com.raffleease.raffleease.Domains.Auth.DTOs.Register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record RegisterPhoneNumber(
        @NotBlank(message = "Must provide a prefix for phone number")
        @Pattern(regexp = "^\\+\\d{1,3}", message = "Must provide a valid prefix")
        String prefix,

        @NotBlank(message = "Must provide a phone number")
        @Pattern(regexp = "^\\d{1,14}$", message = "Must provide a valid phone number")
        String nationalNumber
) { }
