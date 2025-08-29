package com.example.tickets.tickets;

import com.example.tickets.users.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ticket {
  @Id @GeneratedValue
  private UUID id;

  @Column(nullable=false, length=120)
  private String title;

  @Column(columnDefinition="text")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = "ticket_priority")
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private Priority priority;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = "ticket_status")
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private Status status;

  /** JSONB en Postgres (string raw); alternativamente tabla de unión TAGS */
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private String tags;

  @Column(nullable=false)
  private Instant createdAt;

  @Column(nullable=false)
  private Instant updatedAt;

  @Column(nullable=false)
  private UUID createdBy;

  @ManyToOne
  @JoinColumn(name = "assigned_to_id")
  private User assignedTo;

  public enum Priority { LOW, MED, HIGH }
  public enum Status {
      OPEN,
      IN_PROGRESS,
      CLOSED
  }
}
