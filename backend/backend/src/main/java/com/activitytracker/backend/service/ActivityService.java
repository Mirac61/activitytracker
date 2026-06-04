package com.activitytracker.backend.service;

import com.activitytracker.backend.dto.ActivityDto;
import com.activitytracker.backend.entity.Activity;
import com.activitytracker.backend.entity.User;
import com.activitytracker.backend.exception.InvalidActivityException;
import com.activitytracker.backend.mapper.ActivityMapper;
import com.activitytracker.backend.repository.ActivityRepository;
import com.activitytracker.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;
    private final UserRepository userRepository;

    @Transactional
    public void uploadActivity(ActivityDto request, String userId) {

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
            log.info("Activity {} already exists for user {}", activityId, userId);
            return;
        }

        User user = userRepository.findById(UUID.fromString(userId)).
                orElseThrow(() -> new RuntimeException("User not found"));

        var activity = activityMapper.toEntity(request, user);
        activityRepository.save(activity);
    }

    @Transactional
    public void updateActivity(String id, ActivityDto request, String userId) {
        
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