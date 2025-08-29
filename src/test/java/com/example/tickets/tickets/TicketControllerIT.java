package com.example.tickets.tickets;

import com.example.tickets.security.AuthenticationRequest;
import com.example.tickets.tickets.Ticket.Priority;
import com.example.tickets.tickets.Ticket.Status;
import com.example.tickets.users.User;
import com.example.tickets.users.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
//import java.util.List;
//import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TicketControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;
    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() throws Exception {
        ticketRepository.deleteAll();
        userRepository.deleteAll();

        adminUser = userRepository.save(User.builder()
                .email("admin.test@example.com")
                .passwordHash(passwordEncoder.encode("password"))
                .role(User.Role.ADMIN)
                .name("Admin Test")
                .build());

        regularUser = userRepository.save(User.builder()
                .email("user.test@example.com")
                .passwordHash(passwordEncoder.encode("password"))
                .role(User.Role.USER)
                .name("User Test")
                .build());

        adminToken = obtainAuthToken("admin.test@example.com", "password");
        userToken = obtainAuthToken("user.test@example.com", "password");
    }

    private String obtainAuthToken(String email, String password) throws Exception {
        var authRequest = new AuthenticationRequest(email, password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(responseBody);
        return root.get("token").asText();
    }

    @Test
    void shouldForbidAccessToUnauthenticatedUsers() throws Exception {
        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminShouldCreateTicketAndReturnIt() throws Exception {
        var dto = new TicketCreateDto("New Ticket", "A description", Priority.LOW, null, null);

        mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title", is("New Ticket")))
                .andExpect(jsonPath("$.status", is("OPEN")));
    }

    @Test
    void shouldReturnBadRequestForInvalidCreateDto() throws Exception {
        var dto = new TicketCreateDto("", "A description", Priority.LOW, null, null);

        mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void userShouldGetTicketById() throws Exception {
        var ticket = createAndSaveTestTicket("Existing Ticket", Status.OPEN, Priority.MED, adminUser);

        mockMvc.perform(get("/api/tickets/" + ticket.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ticket.getId().toString())))
                .andExpect(jsonPath("$.title", is("Existing Ticket")));
    }

    @Test
    void adminShouldUpdateTicket() throws Exception {
        var ticket = createAndSaveTestTicket("Ticket to Update", Status.OPEN, Priority.LOW, adminUser);
        var dto = new TicketUpdateDto(
                "Updated Title",
                null,
                Priority.HIGH,
                Status.IN_PROGRESS,
                null,
                null
        );

        mockMvc.perform(patch("/api/tickets/" + ticket.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.priority", is("HIGH")))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));
    }

    @Test
    void shouldReturnBadRequestForInvalidUpdateDto() throws Exception {
        var ticket = createAndSaveTestTicket("Ticket to Update", Status.OPEN, Priority.LOW, adminUser);
        var dto = new TicketUpdateDto(
                "", // Invalid: empty title
                null,
                null,
                null,
                null,
                null
        );

        mockMvc.perform(patch("/api/tickets/" + ticket.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void adminShouldDeleteTicket() throws Exception {
        var ticket = createAndSaveTestTicket("Ticket to Delete", Status.OPEN, Priority.LOW, adminUser);

        mockMvc.perform(delete("/api/tickets/" + ticket.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertFalse(ticketRepository.findById(ticket.getId()).isPresent());
    }

    @Test
    void userShouldNotBeAbleToDeleteTicket() throws Exception {
        var ticket = createAndSaveTestTicket("Protected Ticket", Status.OPEN, Priority.LOW, adminUser);

        mockMvc.perform(delete("/api/tickets/" + ticket.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFilterTicketsByStatusAndPriority() throws Exception {
        createAndSaveTestTicket("Ticket 1", Status.OPEN, Priority.HIGH, adminUser);
        createAndSaveTestTicket("Ticket 2", Status.OPEN, Priority.LOW, adminUser);
        createAndSaveTestTicket("Ticket 3", Status.CLOSED, Priority.HIGH, adminUser);

        mockMvc.perform(get("/api/tickets?status=OPEN&priority=HIGH")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("Ticket 1")));
    }

    @Test
    void shouldFilterTicketsByTextQuery() throws Exception {
        createAndSaveTestTicket("Important Database Issue", Status.OPEN, Priority.HIGH, adminUser);
        createAndSaveTestTicket("Frontend login button bug", Status.OPEN, Priority.MED, adminUser);
        createAndSaveTestTicket("Fix database connection pool", Status.IN_PROGRESS, Priority.HIGH, adminUser);

        mockMvc.perform(get("/api/tickets?q=database")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].title", containsInAnyOrder("Important Database Issue", "Fix database connection pool")));
    }

    @Test
    void shouldReturnPagedResults() throws Exception {
        for (int i = 1; i <= 15; i++) {
            createAndSaveTestTicket("Ticket " + i, Status.OPEN, Priority.LOW, adminUser);
        }

        mockMvc.perform(get("/api/tickets?page=1&size=5")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalPages", is(3)))
                .andExpect(jsonPath("$.totalElements", is(15)))
                .andExpect(jsonPath("$.number", is(1)));
    }

    private Ticket createAndSaveTestTicket(String title, Status status, Priority priority, User creator) {
        return ticketRepository.save(Ticket.builder()
                .title(title)
                .description("Description for " + title)
                .status(status)
                .priority(priority)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy(creator.getId())
                .build());
    }
}
