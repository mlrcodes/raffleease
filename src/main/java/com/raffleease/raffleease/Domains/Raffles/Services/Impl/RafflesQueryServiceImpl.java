package com.raffleease.raffleease.Domains.Raffles.Services.Impl;

import com.raffleease.raffleease.Domains.Associations.Model.Association;
import com.raffleease.raffleease.Domains.Associations.Services.AssociationsService;
import com.raffleease.raffleease.Domains.Raffles.DTOs.PublicRaffleDTO;
import com.raffleease.raffleease.Domains.Raffles.Mappers.IRafflesMapper;
import com.raffleease.raffleease.Domains.Raffles.Model.Raffle;
import com.raffleease.raffleease.Domains.Raffles.Repository.RafflesRepository;
import com.raffleease.raffleease.Domains.Raffles.Services.RafflesPersistenceService;
import com.raffleease.raffleease.Domains.Raffles.Services.RafflesQueryService;
import com.raffleease.raffleease.Domains.Tokens.Services.TokensQueryService;
import com.raffleease.raffleease.Exceptions.CustomExceptions.DatabaseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RafflesQueryServiceImpl implements RafflesQueryService {
    private final RafflesPersistenceService rafflesPersistence;
    private final RafflesRepository rafflesRepository;
    private final IRafflesMapper mapper;
    private final AssociationsService associationsService;

    @Override
    public PublicRaffleDTO get(Long id) {
        return mapper.fromRaffle(rafflesPersistence.findById(id));
    }

    @Override
    public List<PublicRaffleDTO> getAll(Long associationId) {
        Association association = associationsService.findById(associationId);
        return mapper.fromRaffleList(findByAssociation(association));
    }

    @Override
    public List<Raffle> findAllByAssociation(Association association) {
        try {
            return rafflesRepository.findAllByAssociation(association);
        } catch (DataAccessException exp) {
            throw new DatabaseException("Database error occurred while retrieving raffles by association: " + exp.getMessage());
        }
    }

    private List<Raffle> findByAssociation(Association association) {
        try {
            return rafflesRepository.findByAssociation(association);
        } catch (DataAccessException exp) {
            throw new DatabaseException("Database error occurred while retrieving raffle by association: " + exp.getMessage());
        }
    }
}
