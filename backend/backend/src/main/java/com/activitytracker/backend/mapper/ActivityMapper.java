package com.activitytracker.backend.mapper;

import com.activitytracker.backend.dto.ActivityDto;
import com.activitytracker.backend.entity.Activity;
import org.springframework.stereotype.Component;
import com.activitytracker.backend.entity.User;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class ActivityMapper {

    public Activity toEntity(ActivityDto dto, UUID id, User user) {
        return new Activity(
                id,
                user,
                dto.activityName(),
                dto.activityDate().atStartOfDay(),
                dto.createdAt() != null ? dto.createdAt() : OffsetDateTime.now()
        );
    }
}
