package com.example.tickets.tickets;

import org.springframework.data.jpa.domain.Specification;

public class TicketSpecification {

    public static Specification<Ticket> hasStatus(Ticket.Status status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Ticket> hasPriority(Ticket.Priority priority) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("priority"), priority);
    }

    public static Specification<Ticket> textInAllColumns(String text) {
        final String finalText = "%" + text.toLowerCase() + "%";
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), finalText),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), finalText)
                );
    }
}
