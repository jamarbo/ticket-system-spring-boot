package com.example.tickets.tickets;

import com.example.tickets.tickets.Ticket.Priority;
import com.example.tickets.tickets.Ticket.Status;

import jakarta.validation.constraints.Size;

import java.util.UUID;

public record TicketUpdateDto(
    @Size(min = 1, max = 120)
    String title,
    String description,
    Priority priority,
    Status status,
    String tags,
    UUID assignedToId
) {
}
