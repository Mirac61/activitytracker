package com.activitytracker.backend.controller;

import com.activitytracker.backend.dto.ActivityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.activitytracker.backend.service.ActivityService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;
    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestBody ActivityDto request) {
        activityService.uploadActivity(request);
        return ResponseEntity.ok().build();
    }
}