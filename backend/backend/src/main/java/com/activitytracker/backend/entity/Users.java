package com.activitytracker.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    @Column(name= "user_id",nullable = false, updatable = false)
    private String userId;

    @Column(nullable = false)
    private String username;
}
