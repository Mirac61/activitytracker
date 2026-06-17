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
    public void uploadActivity(ActivityDto request, UUID userId) {
// TODO:

        Optional<Activity> existing = activityRepository.findById(request.id());

        existing
                .filter(activity -> activity.getUser().getUserId().equals(userId))
                .orElseThrow(()->new AccessDeniedException("Activity belongs to another user"));


        User user = userRepository.findById(userId).
                orElseThrow(() -> new RuntimeException("User not found"));

        var activity = activityMapper.toEntity(request, user);
        activityRepository.save(activity);
    }

    @Transactional
    public void updateActivity(UUID id, ActivityDto request, UUID userId) {

        Activity existing = activityRepository.findById(request.id())
                .orElseThrow(() -> new InvalidActivityException("Activity not found"));

        if (!existing.getUser().getUserId().toString().equals(userId)) {
            throw new AccessDeniedException("Activity belongs to another user");
        }

        existing.setName(request.activityName());
        existing.setTimestamp(request.activityDate().atStartOfDay());

        activityRepository.save(existing);
    }
}