package com.activitytracker.backend.repository;

import com.activitytracker.backend.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {
}