package com.activitytracker.backend.service;

import com.activitytracker.backend.dto.ActivityDto;
import com.activitytracker.backend.exception.InvalidActivityException;
import com.activitytracker.backend.mapper.ActivityMapper;
import com.activitytracker.backend.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;

    @Transactional
    public void uploadActivity(ActivityDto request) {
        if (request.getActivityName() == null || request.getActivityName().isBlank()) {
            throw new InvalidActivityException("Name darf nicht leer sein");
        }
        if (request.getActivityDate() == null) {
            throw new InvalidActivityException("Datum darf nicht leer sein");
        }

        UUID uuid = UUID.fromString(request.getId());

        if (activityRepository.existsById(uuid)) {
            return;
        }

        var activity = activityMapper.toEntity(request);
        activityRepository.save(activity);
    }
}