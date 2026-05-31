package com.activitytracker.backend.mapper;

import com.activitytracker.backend.dto.ActivityDto;
import com.activitytracker.backend.entity.Activity;
import org.springframework.stereotype.Component;
import com.activitytracker.backend.entity.User;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class ActivityMapper {

    public Activity toEntity(ActivityDto dto, User user) {
        return new Activity(
                UUID.fromString(dto.getId()),
                user,
                dto.getActivityName(),
                dto.getActivityDate().atStartOfDay(),
                dto.getCreatedAt() != null ? dto.getCreatedAt() : OffsetDateTime.now()
        );
    }
}
