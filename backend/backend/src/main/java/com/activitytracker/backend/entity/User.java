package com.activitytracker.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private UUID userId;

    @Column(nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String friendCode;
}
