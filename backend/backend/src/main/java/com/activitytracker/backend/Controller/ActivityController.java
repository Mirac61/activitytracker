package com.activitytracker.backend.Controller;

import com.activitytracker.backend.dto.ActivityDto;
import com.activitytracker.backend.entity.Activity;
import com.activitytracker.backend.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityRepository activityRepository;


    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestBody ActivityDto request) {

        try {
            if (request.getActivityName() == null || request.getActivityName().isBlank() ) {
                return ResponseEntity.badRequest().build();
            }
            if (request.getActivityDate() == null ) {
                return ResponseEntity.badRequest().build();
            }
            if (activityRepository.existsById(UUID.fromString(request.getId()))) {
                return ResponseEntity.ok().build();
            }


            Activity activity = new Activity(
                    UUID.fromString(request.getId()),
                    null,
                    request.getActivityName(),
                    request.getActivityDate().atStartOfDay()
            );

            activityRepository.save(activity);
            return ResponseEntity.ok().build();
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }
}