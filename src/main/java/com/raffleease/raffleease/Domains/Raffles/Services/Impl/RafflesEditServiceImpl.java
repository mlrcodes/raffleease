package com.raffleease.raffleease.Domains.Raffles.Services.Impl;

import com.raffleease.raffleease.Domains.Images.DTOs.ImageDTO;
import com.raffleease.raffleease.Domains.Images.Model.Image;
import com.raffleease.raffleease.Domains.Images.Services.ImagesService;
import com.raffleease.raffleease.Domains.Raffles.DTOs.PublicRaffleDTO;
import com.raffleease.raffleease.Domains.Raffles.DTOs.RaffleEdit;
import com.raffleease.raffleease.Domains.Raffles.Mappers.IRafflesMapper;
import com.raffleease.raffleease.Domains.Raffles.Model.Raffle;
import com.raffleease.raffleease.Domains.Raffles.Services.RafflesEditService;
import com.raffleease.raffleease.Domains.Raffles.Services.RafflesPersistenceService;
import com.raffleease.raffleease.Domains.Tickets.DTO.TicketsCreate;
import com.raffleease.raffleease.Domains.Tickets.Model.Ticket;
import com.raffleease.raffleease.Domains.Tickets.Services.ITicketsService;
import com.raffleease.raffleease.Exceptions.CustomExceptions.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RafflesEditServiceImpl implements RafflesEditService {
    private final RafflesPersistenceService rafflesPersistence;
    private final ITicketsService ticketsCreateService;
    private final ImagesService imagesService;
    private final IRafflesMapper rafflesMapper;

    @Transactional
    public PublicRaffleDTO updateStatistics(Long id, RaffleEdit raffleEdit) {
        Raffle raffle = rafflesPersistence.findById(id);

        if (raffleEdit.title() != null) {
            raffle.setTitle(raffleEdit.title());
        }

        if (raffleEdit.description() != null) {
            raffle.setDescription(raffleEdit.description());
        }

        if (raffleEdit.endDate() != null) {
            raffle.setEndDate(raffleEdit.endDate());
        }

        if (raffleEdit.images() != null && !raffleEdit.images().isEmpty()) {
            addNewImages(raffle, raffleEdit.images());
        }

        if (raffleEdit.ticketPrice() != null) {
            raffle.setTicketPrice(raffleEdit.ticketPrice());
        }

        if (raffleEdit.totalTickets() != null) {
            editTotalTickets(raffle, raffleEdit.totalTickets());
        }

        if (raffleEdit.price() != null) {
            raffle.setTicketPrice(raffleEdit.ticketPrice());
        }

        Raffle savedRaffle = rafflesPersistence.save(raffle);
        return rafflesMapper.fromRaffle(savedRaffle);
    }

    @Override
    public void updateStatistics(Raffle raffle, BigDecimal revenue, Long soldTickets) {
        raffle.setSoldTickets(raffle.getSoldTickets() + soldTickets);
        raffle.setRevenue(raffle.getRevenue().add(revenue));
        rafflesPersistence.save(raffle);
    }

    private void addNewImages(Raffle raffle, List<ImageDTO> imageDTOs) {
        List<Image> images = imagesService.associateImagesToRaffleOnEdit(raffle, imageDTOs);
        raffle.setImages(images);
    }

    private void editTotalTickets(Raffle raffle, long editTotal) {
        if (raffle.getSoldTickets() != null && editTotal < raffle.getSoldTickets()) {
            throw new BusinessException("The total tickets count cannot be less than the number of tickets already sold for this raffle");
        }

        long oldTotal = raffle.getTotalTickets();
        raffle.setTotalTickets(editTotal);

        long ticketDifference = editTotal - oldTotal;
        raffle.setAvailableTickets(raffle.getAvailableTickets() + ticketDifference);

        if (ticketDifference > 0) {
            createAdditionalTickets(raffle, ticketDifference);
        }
    }

    private void createAdditionalTickets(Raffle raffle, long amount) {
        long lowerLimit = raffle.getFirstTicketNumber() + raffle.getTotalTickets();

        TicketsCreate request = TicketsCreate.builder()
                .amount(amount)
                .price(raffle.getTicketPrice())
                .lowerLimit(lowerLimit)
                .build();

        List<Ticket> newTickets = ticketsCreateService.create(raffle, request);
        raffle.getTickets().addAll(newTickets);
    }
}