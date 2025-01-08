package com.raffleease.raffleease.Domains.Auth.Services.Impl;

import com.raffleease.raffleease.Domains.Auth.DTOs.AuthRequest;
import com.raffleease.raffleease.Domains.Auth.DTOs.AuthResponse;
import com.raffleease.raffleease.Domains.Auth.Services.ICookiesService;
import com.raffleease.raffleease.Domains.Auth.Services.ILoginService;
import com.raffleease.raffleease.Domains.Token.Services.ITokensCreateService;
import com.raffleease.raffleease.Domains.Users.Model.User;
import com.raffleease.raffleease.Domains.Users.Services.IUsersService;
import com.raffleease.raffleease.Exceptions.CustomExceptions.AuthenticationException;
import com.raffleease.raffleease.Exceptions.CustomExceptions.NotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements ILoginService {
    private final AuthenticationManager authenticationManager;
    private final ITokensCreateService tokensCreateService;
    private final ICookiesService cookiesService;
    private final IUsersService usersService;

    public AuthResponse authenticate(AuthRequest request, HttpServletResponse response) {
        authenticateCredentials(request.email(), request.password());
        User user = findUser(request.email());
        String accessToken = tokensCreateService.generateAccessToken(user);
        String refreshToken = tokensCreateService.generateRefreshToken(user);
        cookiesService.addCookie(response, "refresh_token", refreshToken, 6048000);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    private User findUser(String identifier) {
        try {
            return usersService.findByIdentifier(identifier);
        } catch (NotFoundException ex) {
            throw new AuthenticationException("Authentication failed for provided credentials");
        }
    }

    private void authenticateCredentials(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (org.springframework.security.core.AuthenticationException exp) {
            throw new AuthenticationException("Authentication failed for provided credentials");
        }
    }
}
