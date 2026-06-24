package com.activitytracker.backend.service;

import com.activitytracker.backend.entity.*;
import com.activitytracker.backend.exception.FriendshipException;
import com.activitytracker.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @InjectMocks
    private FriendService friendService;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        sender.setUsername("Alex");
        sender.setFriendCode("Alex-A7KF92Z0");

        receiver = new User();
        receiver.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        receiver.setUsername("Maria");
        receiver.setFriendCode("Maria-B3KP11X9");
    }


    @Test
    void generateFriendCode_noCollision_returnsCode() {
        when(userRepository.existsByFriendCode(anyString())).thenReturn(false);

        String code = friendService.generateFriendCode("Alex");

        assertTrue(code.startsWith("Alex-"));
        assertEquals(13, code.length()); // "Alex-" (5) + 8 Zeichen
    }

    @Test
    void generateFriendCode_firstCollision_retriesAndReturnsCode() {
        when(userRepository.existsByFriendCode(anyString()))
                .thenReturn(true)  // erster Versuch = Kollision
                .thenReturn(false); // zweiter Versuch = frei

        String code = friendService.generateFriendCode("Alex");

        assertTrue(code.startsWith("Alex-"));
        verify(userRepository, times(2)).existsByFriendCode(anyString());
    }


    @Test
    void sendFriendRequest_success() {
        when(userRepository.findById(sender.getUserId())).thenReturn(Optional.of(sender));
        when(userRepository.findByFriendCode(receiver.getFriendCode())).thenReturn(Optional.of(receiver));
        when(friendshipRepository.existsByUserAndFriend(sender, receiver)).thenReturn(false);
        when(friendRequestRepository.existsBySenderAndReceiver(sender, receiver)).thenReturn(false);

        friendService.sendFriendRequest(sender.getUserId(), receiver.getFriendCode());

        verify(friendRequestRepository).save(any(FriendRequest.class));
    }

    @Test
    void sendFriendRequest_toSelf_throwsException() {
        when(userRepository.findById(sender.getUserId())).thenReturn(Optional.of(sender));
        when(userRepository.findByFriendCode(sender.getFriendCode())).thenReturn(Optional.of(sender));

        assertThrows(FriendshipException.class, () ->
                friendService.sendFriendRequest(sender.getUserId(), sender.getFriendCode())
        );

        verify(friendRequestRepository, never()).save(any());
    }

    @Test
    void sendFriendRequest_alreadyFriends_throwsException() {
        when(userRepository.findById(sender.getUserId())).thenReturn(Optional.of(sender));
        when(userRepository.findByFriendCode(receiver.getFriendCode())).thenReturn(Optional.of(receiver));
        when(friendshipRepository.existsByUserAndFriend(sender, receiver)).thenReturn(true);

        assertThrows(FriendshipException.class, () ->
                friendService.sendFriendRequest(sender.getUserId(), receiver.getFriendCode())
        );

        verify(friendRequestRepository, never()).save(any());
    }

    @Test
    void sendFriendRequest_alreadyRequested_throwsException() {
        when(userRepository.findById(sender.getUserId())).thenReturn(Optional.of(sender));
        when(userRepository.findByFriendCode(receiver.getFriendCode())).thenReturn(Optional.of(receiver));
        when(friendshipRepository.existsByUserAndFriend(sender, receiver)).thenReturn(false);
        when(friendRequestRepository.existsBySenderAndReceiver(sender, receiver)).thenReturn(true);

        assertThrows(FriendshipException.class, () ->
                friendService.sendFriendRequest(sender.getUserId(), receiver.getFriendCode())
        );

        verify(friendRequestRepository, never()).save(any());
    }


    @Test
    void acceptFriendRequest_createsTwoFriendships() {
        FriendRequest request = new FriendRequest();
        request.setRequestId(UUID.randomUUID());
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(FriendRequestStatus.PENDING);

        when(friendRequestRepository.findById(request.getRequestId()))
                .thenReturn(Optional.of(request));

        friendService.acceptFriendRequest(request.getRequestId());

        verify(friendshipRepository, times(2)).save(any(Friendship.class));
        assertEquals(FriendRequestStatus.ACCEPTED, request.getStatus());
    }


    @Test
    void declineFriendRequest_setsStatusToDeclined() {
        FriendRequest request = new FriendRequest();
        request.setRequestId(UUID.randomUUID());
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(FriendRequestStatus.PENDING);

        when(friendRequestRepository.findById(request.getRequestId()))
                .thenReturn(Optional.of(request));

        friendService.declineFriendRequest(request.getRequestId());

        assertEquals(FriendRequestStatus.DECLINED, request.getStatus());
        verify(friendRequestRepository).save(request);
    }


    @Test
    void removeFriend_deletesBothEntries() {
        when(userRepository.findById(sender.getUserId())).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiver.getUserId())).thenReturn(Optional.of(receiver));

        friendService.removeFriend(sender.getUserId(), receiver.getUserId());

        verify(friendshipRepository).deleteByUserAndFriend(sender, receiver);
        verify(friendshipRepository).deleteByUserAndFriend(receiver, sender);
    }
}