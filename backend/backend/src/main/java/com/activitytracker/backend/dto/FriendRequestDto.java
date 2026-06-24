package com.activitytracker.backend.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestDto {
    private UUID requestId;
    private String senderUsername;
    private String senderFriendCode;
}