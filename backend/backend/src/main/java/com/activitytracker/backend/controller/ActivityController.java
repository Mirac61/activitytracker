package com.activitytracker.backend.controller;

import com.activitytracker.backend.dto.ActivityDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.activitytracker.backend.service.ActivityService;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@Valid @RequestBody ActivityDto request) {
        // SECURITY NOTE: userId comes from request body, not JWT.
        // It should be extracted from JWT via @AuthenticationPrincipal.
        activityService.uploadActivity(request, request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}