package com.example.tickets.tickets;

import com.example.tickets.users.User;
import com.example.tickets.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void findAllShouldReturnPageOfTickets() {
        // Given
        Pageable pageable = Pageable.unpaged();
        Specification<Ticket> spec = Specification.where(null);
        Ticket ticket = new Ticket();
        Page<Ticket> expectedPage = new PageImpl<>(List.of(ticket));

        when(ticketRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        // When
        Page<Ticket> result = ticketService.findAll(spec, pageable);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        verify(ticketRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void createTicketShouldSaveAndReturnTicket() {
        // Given
        String userEmail = "test@example.com";
        TicketCreateDto createDto = new TicketCreateDto("New Ticket", "Description", Ticket.Priority.MED, "tag1", null);
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(userEmail);

        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        ticket.setTitle(createDto.title());

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userEmail);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(ticketMapper.toEntity(createDto, user)).thenReturn(ticket);
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        // When
        Ticket result = ticketService.createTicket(createDto);

        // Then
        assertNotNull(result);
        assertEquals(ticket.getTitle(), result.getTitle());
        verify(userRepository).findByEmail(userEmail);
        verify(ticketMapper).toEntity(createDto, user);
        verify(ticketRepository).save(ticket);
    }

    @Test
    void updateTicketShouldModifyAndReturnTicket() {
        // Given
        UUID ticketId = UUID.randomUUID();
        UUID assignedToId = UUID.randomUUID();
        TicketUpdateDto updateDto = new TicketUpdateDto("Updated Title", "Updated Desc", Ticket.Priority.HIGH, Ticket.Status.IN_PROGRESS, "tags", assignedToId);
        
        Ticket existingTicket = new Ticket();
        existingTicket.setId(ticketId);
        existingTicket.setTitle("Old Title");

        User assignedUser = new User();
        assignedUser.setId(assignedToId);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(existingTicket));
        when(userRepository.findById(assignedToId)).thenReturn(Optional.of(assignedUser));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Ticket result = ticketService.updateTicket(ticketId, updateDto);

        // Then
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Desc", result.getDescription());
        assertEquals(Ticket.Priority.HIGH, result.getPriority());
        assertEquals(Ticket.Status.IN_PROGRESS, result.getStatus());
        assertEquals(assignedUser, result.getAssignedTo());
        verify(ticketRepository).findById(ticketId);
        verify(userRepository).findById(assignedToId);
        verify(ticketRepository).save(existingTicket);
    }

    @Test
    void updateTicketShouldThrowExceptionWhenNotFound() {
        // Given
        UUID ticketId = UUID.randomUUID();
        TicketUpdateDto updateDto = new TicketUpdateDto("t", "d", null, null, null, null);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TicketNotFoundException.class, () -> {
            ticketService.updateTicket(ticketId, updateDto);
        });

        verify(ticketRepository).findById(ticketId);
        verify(ticketRepository, never()).save(any());
    }
}