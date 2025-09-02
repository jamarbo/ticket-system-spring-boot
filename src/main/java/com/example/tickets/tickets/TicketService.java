package com.example.tickets.tickets;

import com.example.tickets.users.User;
import com.example.tickets.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
//import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.ticketMapper = ticketMapper;
    }

    public Page<Ticket> findAll(Specification<Ticket> spec, Pageable pageable) {
        return ticketRepository.findAll(spec, pageable);
    }

    public Ticket findById(UUID id) {
        return ticketRepository.findById(id).orElse(null);
    }

    @Transactional
    public Ticket createTicket(TicketCreateDto dto) {
        var context = SecurityContextHolder.getContext();
        var authentication = (context != null) ? context.getAuthentication() : null;
        if (authentication == null || authentication.getName() == null || "anonymousUser".equals(authentication.getName())) {
            throw new IllegalStateException("Cannot create ticket without an authenticated user");
        }
        String userEmail = authentication.getName();
            
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("The user for the ticket could not be found in the database: " + userEmail));
        Ticket ticket = ticketMapper.toEntity(dto, currentUser);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket updateTicket(UUID id, TicketUpdateDto dto) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));

        if (dto.title() != null) {
            ticket.setTitle(dto.title());
        }
        if (dto.description() != null) {
            ticket.setDescription(dto.description());
        }
        if (dto.priority() != null) {
            ticket.setPriority(dto.priority());
        }
        if (dto.status() != null) {
            ticket.setStatus(dto.status());
        }
        if (dto.tags() != null) {
            ticket.setTags(dto.tags());
        }
        if (dto.assignedToId() != null) {
            userRepository.findById(dto.assignedToId())
                    .ifPresent(ticket::setAssignedTo);
        }


        ticket.setUpdatedAt(Instant.now());

        return ticketRepository.save(ticket);
    }

    public void deleteTicket(UUID id) {
        ticketRepository.deleteById(id);
    }
}
