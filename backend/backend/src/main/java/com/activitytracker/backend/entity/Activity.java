package com.activitytracker.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;
import java.time.OffsetDateTime;

@Entity
@Table(name = "activities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Activity {
    @Id
    private UUID activityId;
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    @NonNull
    private String name;
    @Column(nullable = false)
    @NonNull
    private LocalDateTime timestamp;
    @Column(nullable = false)
    @NonNull
    private OffsetDateTime createdAt;
}
