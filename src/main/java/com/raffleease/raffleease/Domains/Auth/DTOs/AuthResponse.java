package com.raffleease.raffleease.Domains.Auth.DTOs;

import lombok.Builder;

@Builder
public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}
