package com.activitytracker.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
    @Id
    private final UUID userId = UUID.randomUUID();

    @Column(nullable = false)
    @NonNull String username;
}
