/**
 * Paquete que contiene las clases relacionadas con la gestión de tickets.
 * 
 * <p>Este paquete incluye:</p>
 * <ul>
 *   <li>{@link com.example.tickets.tickets.Ticket} - Entidad principal del ticket</li>
 *   <li>{@link com.example.tickets.tickets.TicketController} - Controlador REST para tickets</li>
 *   <li>{@link com.example.tickets.tickets.TicketService} - Lógica de negocio para tickets</li>
 *   <li>{@link com.example.tickets.tickets.TicketRepository} - Repositorio de datos para tickets</li>
 *   <li>{@link com.example.tickets.tickets.TicketCreateDto} - DTO para crear tickets</li>
 *   <li>{@link com.example.tickets.tickets.TicketUpdateDto} - DTO para actualizar tickets</li>
 *   <li>{@link com.example.tickets.tickets.TicketMapper} - Mapper entre entidades y DTOs</li>
 *   <li>{@link com.example.tickets.tickets.TicketSpecification} - Especificaciones JPA para filtrado</li>
 *   <li>{@link com.example.tickets.tickets.TicketNotFoundException} - Excepción para tickets no encontrados</li>
 * </ul>
 * 
 * <p>Los tickets pueden tener diferentes estados ({@link com.example.tickets.tickets.Ticket.Status})
 * y prioridades ({@link com.example.tickets.tickets.Ticket.Priority}).</p>
 * 
 * @since 1.0
 * @version 1.0
 */
package com.example.tickets.tickets;