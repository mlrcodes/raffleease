package com.raffleease.raffleease.Domains.Raffles.Controller;

import com.raffleease.raffleease.Domains.Raffles.Model.Raffle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static com.raffleease.raffleease.Domains.Raffles.Model.RaffleStatus.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RafflesControllerDeleteIT extends BaseRafflesIT {
    private Long raffleId;

    @BeforeEach
    void setUp() throws Exception {
        raffleId = createRaffle(associationId, accessToken);
    }

    @Test
    void shouldDeletePendingRaffleSuccessfully() throws Exception {
        // 1. Perform delete
        performDelete().andExpect(status().isNoContent());

        // 2. Verify it's no longer in DB
        Optional<Raffle> raffle = rafflesRepository.findById(raffleId);
        assertThat(raffle).isEmpty();
    }

    @Test
    void shouldFailToDeleteIfRaffleIsActive() throws Exception {
        // Update to ACTIVE
        performUpdateStatusRequest(raffleId, ACTIVE)
                .andExpect(status().isOk());

        performDelete()
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Only raffles in 'PENDING' state can be deleted."));

        // Should still exist
        assertThat(rafflesRepository.findById(raffleId)).isPresent();
    }

    @Test
    void shouldFailToDeleteIfRaffleIsPaused() throws Exception {
        // Update to ACTIVE → PAUSED
        performUpdateStatusRequest(raffleId, ACTIVE).andExpect(status().isOk());
        performUpdateStatusRequest(raffleId, PAUSED).andExpect(status().isOk());

        performDelete()
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Only raffles in 'PENDING' state can be deleted."));

        assertThat(rafflesRepository.findById(raffleId)).isPresent();
    }

    @Test
    void shouldFailToDeleteIfRaffleIsCompleted() throws Exception {
        // Simulate raffle status manually
        Raffle raffle = rafflesRepository.findById(raffleId).orElseThrow();
        raffle.setStatus(COMPLETED);
        rafflesRepository.save(raffle);

        performDelete()
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Only raffles in 'PENDING' state can be deleted."));

        assertThat(rafflesRepository.findById(raffleId)).isPresent();
    }

    private ResultActions performDelete() throws Exception {
        return mockMvc.perform(delete("/api/v1/associations/" + associationId + "/raffles/" + raffleId)
                .header(AUTHORIZATION, "Bearer " + accessToken));
    }
}