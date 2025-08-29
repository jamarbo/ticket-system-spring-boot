package com.example.tickets.tickets;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
//import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<Page<Ticket>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Ticket.Status status,
            @RequestParam(required = false) Ticket.Priority priority,
            Pageable pageable
    ) {
        Specification<Ticket> spec = Specification.where(null);

        if (q != null && !q.isBlank()) {
            spec = spec.and(TicketSpecification.textInAllColumns(q));
        }
        if (status != null) {
            spec = spec.and(TicketSpecification.hasStatus(status));
        }
        if (priority != null) {
            spec = spec.and(TicketSpecification.hasPriority(priority));
        }

        return ResponseEntity.ok(ticketService.findAll(spec, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getById(@PathVariable UUID id) {
        Ticket ticket = ticketService.findById(id);
        return ticket != null ? ResponseEntity.ok(ticket) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Ticket> create(@Valid @RequestBody TicketCreateDto dto) {
        Ticket createdTicket = ticketService.createTicket(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTicket.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdTicket);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Ticket> update(@PathVariable UUID id, @Valid @RequestBody TicketUpdateDto dto) {
        Ticket updatedTicket = ticketService.updateTicket(id, dto);
        return updatedTicket != null ? ResponseEntity.ok(updatedTicket) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}

