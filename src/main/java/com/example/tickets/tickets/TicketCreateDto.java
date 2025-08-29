package com.example.tickets.tickets;

import com.example.tickets.tickets.Ticket.Priority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record TicketCreateDto(
    @NotBlank @Size(max = 120)
    String title,

    String description,

    @NotNull
    Priority priority,

    String tags,

    UUID assignedToId
) {
}
