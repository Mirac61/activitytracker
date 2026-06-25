package com.activitytracker.backend.controller;

import com.activitytracker.backend.TestSecurityConfig;
import com.activitytracker.backend.entity.*;
import com.activitytracker.backend.repository.*;
import com.activitytracker.backend.service.KeycloakService;
import com.activitytracker.backend.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
        })
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class FriendControllerTest {

    @MockitoBean
    private KeycloakService keycloakService;

    @MockitoBean
    private WeatherService weatherService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        friendRequestRepository.deleteAllInBatch();
        friendshipRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        sender = new User();
        sender.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        sender.setUsername("Alex");
        sender.setFriendCode("Alex-A7KF92Z0");
        userRepository.save(sender);

        receiver = new User();
        receiver.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        receiver.setUsername("Maria");
        receiver.setFriendCode("Maria-B3KP11X9");
        userRepository.save(receiver);
    }


    @Test
    void getFriends_noFriends_returnsEmptyList() throws Exception {
        mvc.perform(get("/friends/{userId}", sender.getUserId()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getFriends_withFriend_returnsFriendList() throws Exception {
        Friendship friendship = new Friendship();
        friendship.setUser(sender);
        friendship.setFriend(receiver);
        friendshipRepository.save(friendship);

        mvc.perform(get("/friends/{userId}", sender.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Maria"))
                .andExpect(jsonPath("$[0].friendCode").value("Maria-B3KP11X9"));
    }


    @Test
    void sendFriendRequest_success_returnsOk() throws Exception {
        mvc.perform(post("/friends/{userId}/request/{friendCode}",
                        sender.getUserId(), receiver.getFriendCode()))
                .andExpect(status().isOk());
    }

    @Test
    void sendFriendRequest_toSelf_returnsBadRequest() throws Exception {
        mvc.perform(post("/friends/{userId}/request/{friendCode}",
                        sender.getUserId(), sender.getFriendCode()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendFriendRequest_alreadyFriends_returnsBadRequest() throws Exception {
        Friendship f1 = new Friendship();
        f1.setUser(sender);
        f1.setFriend(receiver);
        friendshipRepository.save(f1);

        mvc.perform(post("/friends/{userId}/request/{friendCode}",
                        sender.getUserId(), receiver.getFriendCode()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendFriendRequest_alreadyRequested_returnsBadRequest() throws Exception {
        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(FriendRequestStatus.PENDING);
        friendRequestRepository.save(request);

        mvc.perform(post("/friends/{userId}/request/{friendCode}",
                        sender.getUserId(), receiver.getFriendCode()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendFriendRequest_unknownFriendCode_returnsNotFound() throws Exception {
        mvc.perform(post("/friends/{userId}/request/{friendCode}",
                        sender.getUserId(), "Unknown-00000000"))
                .andExpect(status().isNotFound());
    }


    @Test
    void getPendingRequests_noRequests_returnsEmptyList() throws Exception {
        mvc.perform(get("/friends/{userId}/requests", receiver.getUserId()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getPendingRequests_withRequest_returnsRequestList() throws Exception {
        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(FriendRequestStatus.PENDING);
        friendRequestRepository.save(request);

        mvc.perform(get("/friends/{userId}/requests", receiver.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].senderUsername").value("Alex"))
                .andExpect(jsonPath("$[0].senderFriendCode").value("Alex-A7KF92Z0"));
    }


    @Test
    void acceptFriendRequest_success_returnsOk() throws Exception {
        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(FriendRequestStatus.PENDING);
        friendRequestRepository.save(request);

        mvc.perform(post("/friends/requests/{requestId}/accept",
                        request.getRequestId()))
                .andExpect(status().isOk());
    }

    @Test
    void acceptFriendRequest_unknownId_returnsNotFound() throws Exception {
        mvc.perform(post("/friends/requests/{requestId}/accept",
                        UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }


    @Test
    void declineFriendRequest_success_returnsOk() throws Exception {
        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(FriendRequestStatus.PENDING);
        friendRequestRepository.save(request);

        mvc.perform(post("/friends/requests/{requestId}/decline",
                        request.getRequestId()))
                .andExpect(status().isOk());
    }


    @Test
    void removeFriend_success_returnsOk() throws Exception {
        Friendship f1 = new Friendship();
        f1.setUser(sender);
        f1.setFriend(receiver);
        friendshipRepository.save(f1);

        Friendship f2 = new Friendship();
        f2.setUser(receiver);
        f2.setFriend(sender);
        friendshipRepository.save(f2);

        mvc.perform(delete("/friends/{userId}/remove/{friendId}",
                        sender.getUserId(), receiver.getUserId()))
                .andExpect(status().isOk());
    }

    @Test
    void removeFriend_unknownUser_returnsNotFound() throws Exception {
        mvc.perform(delete("/friends/{userId}/remove/{friendId}",
                        UUID.randomUUID(), receiver.getUserId()))
                .andExpect(status().isNotFound());
    }
}