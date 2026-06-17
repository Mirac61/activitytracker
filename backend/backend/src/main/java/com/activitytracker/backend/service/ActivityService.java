package com.activitytracker.backend.service;

import com.activitytracker.backend.dto.ActivityDto;
import com.activitytracker.backend.entity.Activity;
import com.activitytracker.backend.entity.User;
import com.activitytracker.backend.exception.UserDoesNotExistException;
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
    public boolean saveActivity(UUID id, ActivityDto request, UUID userId) {

        Optional<Activity> existing = activityRepository.findById(id);

        existing.ifPresent(activity -> {
            if (!activity.getUser().getUserId().equals(userId)) {
                throw new AccessDeniedException("Activity belongs to another user");
            }
        });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("User not found"));

        boolean isNew = existing.isEmpty();

        Activity activity = existing
                .map(a -> {
                    a.setName(request.activityName());
                    a.setTimestamp(request.activityDate().atStartOfDay());
                    return a;
                })
                .orElseGet(() -> activityMapper.toEntity(request, id, user));

        activityRepository.save(activity);

        return isNew;
    }
}