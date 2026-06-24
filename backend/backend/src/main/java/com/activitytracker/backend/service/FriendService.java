package com.activitytracker.backend.service;

import com.activitytracker.backend.dto.FriendDto;
import com.activitytracker.backend.entity.*;
import com.activitytracker.backend.exception.FriendshipException;
import com.activitytracker.backend.exception.NotFoundException;
import com.activitytracker.backend.repository.*;
import com.activitytracker.backend.entity.Activity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final ActivityRepository activityRepository;


    public String generateFriendCode(String username) {
        String code;
        do {
            code = username + "-" + randomPart();
        } while (userRepository.existsByFriendCode(code));
        return code;
    }

    private String randomPart() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }


    public void sendFriendRequest(UUID senderId, String friendCode) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("Sender nicht gefunden"));

        User receiver = userRepository.findByFriendCode(friendCode)
                .orElseThrow(() -> new NotFoundException("Kein User mit diesem friendCode gefunden"));

        if (sender.getUserId().equals(receiver.getUserId())) {
            throw new FriendshipException("Du kannst dir selbst keine Anfrage schicken");
        }

        if (friendshipRepository.existsByUserAndFriend(sender, receiver)) {
            throw new FriendshipException("Ihr seid bereits befreundet");
        }

        if (friendRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new FriendshipException("Anfrage wurde bereits gesendet");
        }

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(FriendRequestStatus.PENDING);
        friendRequestRepository.save(request);
    }


    public List<FriendRequest> getPendingRequests(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User nicht gefunden"));
        return friendRequestRepository.findByReceiverAndStatus(user, FriendRequestStatus.PENDING);
    }


    @Transactional
    public void acceptFriendRequest(UUID requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Anfrage nicht gefunden"));

        request.setStatus(FriendRequestStatus.ACCEPTED);
        friendRequestRepository.save(request);

        Friendship f1 = new Friendship();
        f1.setUser(request.getSender());
        f1.setFriend(request.getReceiver());

        Friendship f2 = new Friendship();
        f2.setUser(request.getReceiver());
        f2.setFriend(request.getSender());

        friendshipRepository.save(f1);
        friendshipRepository.save(f2);
    }


    public void declineFriendRequest(UUID requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Anfrage nicht gefunden"));

        request.setStatus(FriendRequestStatus.DECLINED);
        friendRequestRepository.save(request);
    }


    @Transactional
    public void removeFriend(UUID userId, UUID friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User nicht gefunden"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Freund nicht gefunden"));

        friendshipRepository.deleteByUserAndFriend(user, friend);
        friendshipRepository.deleteByUserAndFriend(friend, user);
    }


    public List<FriendDto> getFriends(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User nicht gefunden"));
        return friendshipRepository.findByUser(user).stream()
                .map(friendship -> {
                    User friend = friendship.getFriend();
                    List<Activity> activities = activityRepository.findByUserUserId(friend.getUserId());                    int streak = StreakCalculator.calculate(activities);
                    return new FriendDto(
                            friend.getUserId(),
                            friend.getUsername(),
                            friend.getFriendCode(),
                            streak
                    );
                })
                .toList();
    }
}