package com.raffleease.raffleease.Domains.Notifications.Model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EmailTemplate {
    ORDER_SUCCESS("order-success.html", "Order successfully completed"),
    EMAIL_VERIFICATION("email-verification.html", "Verify your email");

    private final String template;
    private final String subject;
}