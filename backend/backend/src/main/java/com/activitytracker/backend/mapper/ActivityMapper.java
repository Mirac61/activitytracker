package com.activitytracker.backend.mapper;

import com.activitytracker.backend.dto.ActivityDto;
import com.activitytracker.backend.entity.Activity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class ActivityMapper {

    public Activity toEntity(ActivityDto dto) {
        return new Activity(
                UUID.fromString(dto.getId()),
                null,
                dto.getActivityName(),
                dto.getActivityDate().atStartOfDay(),
                dto.getCreatedAt() != null ? dto.getCreatedAt() : OffsetDateTime.now()
        );
    }
}
