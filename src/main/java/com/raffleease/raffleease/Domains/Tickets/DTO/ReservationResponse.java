package com.raffleease.raffleease.Domains.Tickets.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

@Builder
public record ReservationResponse(
        @NotNull(message = "Tickets cannot be null")
        @NotEmpty(message = "Must select at least one tickets")
        Set<TicketDTO> tickets,

        @NotBlank(message = "Must indicate reservation flag")
        String reservationFlag
) { }