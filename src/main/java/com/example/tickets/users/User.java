
package com.example.tickets.users;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
  @Id @GeneratedValue
  private UUID id;

  @Column(nullable=false, unique=true, length=120)
  private String email;

  @Column(nullable=false)
  private String passwordHash;

  @Column(nullable=false, length=120)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable=false, length=10)
  private Role role;

  public enum Role { ADMIN, USER }
}
