package com.raffleease.raffleease.Domains.Raffles.Services.Impl;

import com.raffleease.raffleease.Domains.Raffles.DTOs.RaffleCreate;
import com.raffleease.raffleease.Domains.Raffles.DTOs.RaffleDTO;
import com.raffleease.raffleease.Domains.Raffles.Mappers.ImagesMapper;
import com.raffleease.raffleease.Domains.Raffles.Mappers.RafflesMapper;
import com.raffleease.raffleease.Domains.Raffles.Model.Raffle;
import com.raffleease.raffleease.Domains.Raffles.Model.RaffleImage;
import com.raffleease.raffleease.Domains.Raffles.Services.IRaffleCommandService;
import com.raffleease.raffleease.Domains.Raffles.Services.IRaffleCreateService;
import com.raffleease.raffleease.Domains.Raffles.Services.IRaffleImagesService;
import com.raffleease.raffleease.Domains.Raffles.Services.RafflesQueryService;
import com.raffleease.raffleease.Domains.Tickets.Model.Ticket;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.raffleease.raffleease.Domains.Raffles.Model.RaffleStatus.PENDING;

@RequiredArgsConstructor
@Service
public class RaffleCreateServiceImpl implements IRaffleCreateService {
    private final RafflesQueryService queryService;
    private final IRaffleCommandService commandService;
    private final IRaffleImagesService imagesService;
    private final TicketsCreateService ticketsCreateService;
    private final RafflesMapper rafflesMapper;
    private final ImagesMapper imagesMapper;

    @Value("${RAFFLE_CLIENT_HOST}")
    private String host;

    @Value("${RAFFLE_CLIENT_PATH}")
    private String path;

    @Transactional
    public RaffleDTO createRaffle(RaffleCreate request) {
        // Long associationId = authClient.getId(authHeader);
        Raffle raffle = rafflesMapper.toRaffle(request);
        // raffle.setAssociation(association);
        Set<Ticket> createdTickets = ticketsCreateService.createTickets(request.ticketsInfo());
        raffle.setTickets(createdTickets);
        raffle.setStatus(PENDING);
        Raffle savedRaffle = commandService.saveRaffle(raffle);
        raffle.setURL(host + path + raffle.getId());
        setRaffleImages(request.imageKeys(), savedRaffle);
        Raffle updatedRaffle = commandService.saveRaffle(savedRaffle);
        setTicketsRaffle(raffle.getId(), createdTickets);
        return rafflesMapper.fromRaffle(updatedRaffle);
    }

    private void setTicketsRaffle(Long raffleId, Set<String> tickets) {
        ticketsRaffleProducer.produceTicketsRaffle(
                TicketsRaffle.builder()
                        .raffleId(raffleId)
                        .tickets(tickets)
                        .build()
        );
    }

    private void setRaffleImages(List<String> imageKeys, Raffle raffle) {
        if (imageKeys == null) return;
        List<RaffleImage> images = imagesMapper.toImages(imageKeys, raffle);
        List<RaffleImage> savedImages = imagesService.saveImages(images);
        raffle.setImages(savedImages);
    }
}