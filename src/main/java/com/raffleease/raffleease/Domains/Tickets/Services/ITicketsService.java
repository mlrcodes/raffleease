package com.raffleease.raffleease.Domains.Tickets.Services;

import com.raffleease.raffleease.Domains.Raffles.Model.Raffle;
import com.raffleease.raffleease.Domains.Tickets.DTO.TicketsCreate;
import com.raffleease.raffleease.Domains.Tickets.Model.Ticket;
import com.raffleease.raffleease.Domains.Tickets.Model.TicketStatus;

import java.util.List;

public interface ITicketsService {
    List<Ticket> create(Raffle raffle, TicketsCreate request);
    List<Ticket> edit(List<Ticket> tickets, TicketStatus status);
}
