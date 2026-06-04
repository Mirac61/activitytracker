package com.activitytracker.backend.service;

import com.activitytracker.backend.dto.ActivityDto;
import com.activitytracker.backend.entity.Activity;
import com.activitytracker.backend.entity.User;
import com.activitytracker.backend.exception.InvalidActivityException;
import com.activitytracker.backend.mapper.ActivityMapper;
import com.activitytracker.backend.repository.ActivityRepository;
import com.activitytracker.backend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;
    private final UserRepository userRepository;

    @Transactional
    public void uploadActivity(ActivityDto request, String userId) {

        if (request.getId() == null || request.getId().isBlank()) {
            throw new InvalidActivityException("Id must not be empty");
        }
        if (request.getActivityName() == null || request.getActivityName().isBlank()) {
            throw new InvalidActivityException("Name can not be empty");
        }
        if (request.getActivityDate() == null) {
            throw new InvalidActivityException("Date can not be empty");
        }

        UUID activityId;
        try {
            activityId = UUID.fromString(request.getId());
        } catch (IllegalArgumentException e) {
            throw new InvalidActivityException("Invalid UUID format for activity ID");
        }

        Optional<Activity> existing = activityRepository.findById(activityId);
        if (existing.isPresent()) {
            if (!existing.get().getUser().getUserId().toString().equals(userId)) {
                throw new AccessDeniedException("Activity belongs to another user");
            }
            // Activity already exists for this user
            return;
        }

        User user = userRepository.findById(UUID.fromString(userId)).
                orElseThrow(() -> new RuntimeException("User not found"));

        var activity = activityMapper.toEntity(request, user);
        activityRepository.save(activity);
    }

    @Transactional
    public void updateActivity(String id, ActivityDto request, String userId) {

        if (id == null || id.isBlank()) {
            throw new InvalidActivityException("Id must not be empty");
        }
        if (request.getActivityName() == null || request.getActivityName().isBlank()) {
            throw new InvalidActivityException("Name can not be empty");
        }
        if (request.getActivityDate() == null) {
            throw new InvalidActivityException("Date can not be empty");
        }

        UUID activityId;
        try {
            activityId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidActivityException("Invalid UUID format for activity ID");
        }

        Activity existing = activityRepository.findById(activityId)
                .orElseThrow(() -> new InvalidActivityException("Activity not found"));

        if (!existing.getUser().getUserId().toString().equals(userId)) {
            throw new AccessDeniedException("Activity belongs to another user");
        }

        existing.setName(request.getActivityName());
        existing.setTimestamp(request.getActivityDate().atStartOfDay());

        activityRepository.save(existing);
    }
}