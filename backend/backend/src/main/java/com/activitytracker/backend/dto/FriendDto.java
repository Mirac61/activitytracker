package com.activitytracker.backend.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendDto {
    private UUID friendId;
    private String username;
    private String friendCode;
}