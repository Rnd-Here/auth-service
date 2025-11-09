package com.cinema.auth.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "userDetails")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private String role; // CREATOR, PRODUCER, FANATIC, ADMIN
}
