package com.example.tickets.tickets;

import org.springframework.stereotype.Component;

import com.example.tickets.users.User;
import com.example.tickets.users.UserRepository;

import java.time.Instant;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TicketMapper {
    private final UserRepository userRepository;

    public Ticket toEntity(TicketCreateDto dto, User currentUser) {
        var now = Instant.now();
        var builder = Ticket.builder()
                .title(dto.title())
                .description(dto.description())
                .priority(dto.priority())
                .status(Ticket.Status.OPEN)
                .tags(dto.tags())
                .createdAt(now)
                .updatedAt(now)
                .createdBy(currentUser.getId());

        if (dto.assignedToId() != null) {
            userRepository.findById(dto.assignedToId())
                .ifPresent(builder::assignedTo);
        }

        return builder.build();
    }
}
