package com.activitytracker.backend.controller;

import com.activitytracker.backend.dto.ActivityDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.activitytracker.backend.service.ActivityService;

import java.util.UUID;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @PutMapping("/{id}")
    public ResponseEntity<Void> save(
            @PathVariable UUID id,
            @Valid @RequestBody ActivityDto request
    ) {
        boolean created = activityService.saveActivity(id, request, request.userId());

        return created
                ? ResponseEntity.status(HttpStatus.CREATED).build()
                : ResponseEntity.noContent().build();
    }
}