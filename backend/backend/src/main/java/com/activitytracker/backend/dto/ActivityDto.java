package com.activitytracker.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ActivityDto {
    private String id;
    private String activityName;
    private LocalDate activityDate;
    private String userId;
}
