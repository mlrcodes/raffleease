package com.raffleease.raffleease.Domains.Raffles.Controller;

import com.raffleease.raffleease.Domains.Raffles.DTOs.RaffleCreate;
import com.raffleease.raffleease.Domains.Raffles.DTOs.RaffleDTO;
import com.raffleease.raffleease.Domains.Raffles.DTOs.RaffleEdit;
import com.raffleease.raffleease.Domains.Raffles.Services.IRafflesOrchestrator;
import com.raffleease.raffleease.Responses.ApiResponse;
import com.raffleease.raffleease.Responses.ResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/raffles")
public class RafflesController {
    private final IRafflesOrchestrator orchestrator;

    @PostMapping
    public ResponseEntity<ApiResponse> create(
            @Valid @RequestBody RaffleCreate request
    ) {
        RaffleDTO createdRaffle = orchestrator.createRaffle(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdRaffle.id())
                .toUri();

        return ResponseEntity.created(location).body(
                ResponseFactory.success(
                        createdRaffle,
                        "New raffle created successfully"
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> edit(
            @PathVariable Long id,
            @Valid @RequestBody RaffleEdit editRaffle
    ) {
        return ResponseEntity.ok(ResponseFactory.success(
                orchestrator.edit(id, editRaffle),
                "Raffle edited successfully"
        ));
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<ApiResponse> publish(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ResponseFactory.success(
                orchestrator.publish(id),
                "Raffle published successfully"
        ));
    }

    @PatchMapping("/{id}/pause")
    public ResponseEntity<ApiResponse> pause(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ResponseFactory.success(
                orchestrator.pause(id),
                "Raffle paused successfully"
        ));
    }

    @PatchMapping("/{id}/restart")
    public ResponseEntity<ApiResponse> restart(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ResponseFactory.success(
                orchestrator.restart(id),
                "Raffle restarted successfully"
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> get(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ResponseFactory.success(
                orchestrator.get(id),
                "Raffle retrieved successfully"
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAll() {
        return ResponseEntity.ok(ResponseFactory.success(
                orchestrator.getAll(),
                "All raffles retrieved successfully"
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long id
    ) {
        orchestrator.delete(id);
        return ResponseEntity.noContent().build();
    }
}
