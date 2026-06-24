package com.activitytracker.backend.controller;

import com.activitytracker.backend.dto.FriendDto;
import com.activitytracker.backend.dto.FriendRequestDto;
import com.activitytracker.backend.entity.FriendRequest;
import com.activitytracker.backend.entity.Friendship;
import com.activitytracker.backend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;


    @GetMapping("/{userId}")
    public ResponseEntity<List<FriendDto>> getFriends(@PathVariable UUID userId) {
        List<FriendDto> friends = friendService.getFriends(userId);
        return ResponseEntity.ok(friends);
    }


    @PostMapping("/{userId}/request/{friendCode}")
    public ResponseEntity<Void> sendRequest(
            @PathVariable UUID userId,
            @PathVariable String friendCode) {
        friendService.sendFriendRequest(userId, friendCode);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<FriendRequestDto>> getPendingRequests(@PathVariable UUID userId) {
        List<FriendRequest> requests = friendService.getPendingRequests(userId);
        List<FriendRequestDto> result = requests.stream()
                .map(r -> new FriendRequestDto(
                        r.getRequestId(),
                        r.getSender().getUsername(),
                        r.getSender().getFriendCode()
                ))
                .toList();
        return ResponseEntity.ok(result);
    }


    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<Void> acceptRequest(@PathVariable UUID requestId) {
        friendService.acceptFriendRequest(requestId);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/requests/{requestId}/decline")
    public ResponseEntity<Void> declineRequest(@PathVariable UUID requestId) {
        friendService.declineFriendRequest(requestId);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{userId}/remove/{friendId}")
    public ResponseEntity<Void> removeFriend(
            @PathVariable UUID userId,
            @PathVariable UUID friendId) {
        friendService.removeFriend(userId, friendId);
        return ResponseEntity.ok().build();
    }
}