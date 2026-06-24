package com.activitytracker.backend.service;

import com.activitytracker.backend.entity.Activity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StreakCalculator {

    public static int calculate(List<Activity> activities) {
        if (activities == null || activities.isEmpty()) return 0;

        Set<LocalDate> activeDays = activities.stream()
                .map(a -> a.getActivityDate().toLocalDate())
                .collect(Collectors.toSet());

        LocalDate today = LocalDate.now();
        int streak = 0;
        LocalDate current = activeDays.contains(today) ? today : today.minusDays(1);

        while (activeDays.contains(current)) {
            streak++;
            current = current.minusDays(1);
        }

        return streak;
    }
}