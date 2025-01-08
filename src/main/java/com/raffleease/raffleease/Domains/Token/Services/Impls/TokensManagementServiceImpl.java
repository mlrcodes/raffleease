package com.raffleease.raffleease.Domains.Token.Services.Impls;

import com.raffleease.raffleease.Domains.Auth.DTOs.AuthResponse;
import com.raffleease.raffleease.Domains.Auth.Services.ICookiesService;
import com.raffleease.raffleease.Domains.Token.Services.*;
import com.raffleease.raffleease.Domains.Users.Model.User;
import com.raffleease.raffleease.Domains.Users.Model.UserPrincipal;
import com.raffleease.raffleease.Domains.Users.Services.IUsersService;
import com.raffleease.raffleease.Exceptions.CustomExceptions.AuthorizationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class TokensManagementServiceImpl implements ITokensManagementService {
    private final ITokensCreateService tokensCreateService;
    private final ITokensValidateService tokensValidateService;
    private final ITokensQueryService tokensQueryService;
    private final IUsersService usersService;
    private final IBlackListService blackListService;
    private final ICookiesService cookiesService;

    @Override
    public AuthResponse refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = cookiesService.extractCookieValue(request, "refresh_token");
        Long userId = getUserIdFromToken(refreshToken);
        User user = usersService.findById(userId);
        tokensValidateService.validateToken(refreshToken, new UserPrincipal(user));
        String accessToken = tokensCreateService.generateAccessToken(user);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    @Override
    public void revoke(String token) {
        String tokenId = tokensQueryService.getTokenId(token);
        if (blackListService.isTokenBlackListed(tokenId)) throw new AuthorizationException("Token already revoked");
        Date expiration = tokensQueryService.getExpiration(token);
        Long expirationTime = expiration.getTime() - System.currentTimeMillis();
        blackListService.addTokenToBlackList(tokenId, expirationTime);
    }

    private Long getUserIdFromToken(String token) {
        String subject = tokensQueryService.getSubject(token);
        if (Objects.isNull(subject)) throw new AuthorizationException("Subject not found in refresh token");
        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException ex) {
            throw new AuthorizationException("Invalid subject format in token");
        }
    }
}
