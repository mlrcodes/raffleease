package com.raffleease.raffleease.Domains.Tickets.Controller;

import com.raffleease.raffleease.Domains.Auth.Validations.ValidateAssociationAccess;
import com.raffleease.raffleease.Domains.Tickets.DTO.*;
import com.raffleease.raffleease.Domains.Tickets.Model.TicketStatus;
import com.raffleease.raffleease.Domains.Tickets.Services.TicketsQueryService;
import com.raffleease.raffleease.Responses.ApiResponse;
import com.raffleease.raffleease.Responses.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ValidateAssociationAccess
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/associations/{associationId}/raffles/{raffleId}/tickets")
public class TicketsController {
    private final TicketsQueryService queryService;

    @GetMapping
    public ResponseEntity<ApiResponse> get(
            @PathVariable Long associationId,
            @PathVariable Long raffleId,
            TicketsSearchFilters searchFilters,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                ResponseFactory.success(
                        queryService.search(associationId, raffleId, searchFilters, pageable),
                        "Ticket retrieved successfully"
                )
        );
    }

     @GetMapping("/random")
     public ResponseEntity<ApiResponse> getRandom(
             @PathVariable Long raffleId,
             @RequestParam Long quantity
     ) {
         List<TicketDTO> tickets = queryService.getRandom(raffleId, quantity);
         return ResponseEntity.ok(
                 ResponseFactory.success(
                         tickets,
                         "Random tickets retrieved successfully"
                 )
         );
     }
}