package com.activitytracker.backend.service;

import com.activitytracker.backend.dto.ActivityDto;
import com.activitytracker.backend.entity.Activity;
import com.activitytracker.backend.entity.User;
import com.activitytracker.backend.exception.InvalidActivityException;
import com.activitytracker.backend.mapper.ActivityMapper;
import com.activitytracker.backend.repository.ActivityRepository;
import com.activitytracker.backend.repository.UserRepository;
import jakarta.ws.rs.ForbiddenException;
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
        UUID activityId = UUID.fromString(request.getId());

        Optional<Activity> existing = activityRepository.findById(activityId);
        if (existing.isPresent()) {
            if (!existing.get().getUser().getUserId().toString().equals(userId)) {
                throw new ForbiddenException("Activity belongs to another user");
            }
            return;
        }
        if (request.getActivityName() == null || request.getActivityName().isBlank()) {
            throw new InvalidActivityException("Name can not be empty");
        }
        if (request.getActivityDate() == null) {
            throw new InvalidActivityException("Date can not be empty");
        }

        User user = userRepository.findById(UUID.fromString(userId)).
                orElseThrow(() -> new RuntimeException("User not found"));

        var activity = activityMapper.toEntity(request);
        activity.setUser(user);
        activityRepository.save(activity);
    }
}