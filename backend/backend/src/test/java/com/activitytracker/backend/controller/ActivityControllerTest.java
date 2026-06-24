package com.activitytracker.backend.controller;

import com.activitytracker.backend.service.KeycloakService;
import com.activitytracker.backend.service.WeatherService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.activitytracker.backend.repository.ActivityRepository;
import com.activitytracker.backend.repository.UserRepository;
import com.activitytracker.backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
        })
@AutoConfigureMockMvc
class ActivityControllerTest {

    @MockitoBean
    private KeycloakService keycloakService;

    @MockitoBean
    private WeatherService weatherService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ActivityRepository repository;

    @Autowired
    private UserRepository userRepository;

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID ACTIVITY_ID_ONE = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ACTIVITY_ID_TWO = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @BeforeEach
    void setUp() {
        repository.deleteAllInBatch();
        if (userRepository.findById(USER_ID).isEmpty()) {
            User user = new User();
            user.setUserId(USER_ID);
            user.setUsername("TestUser");
            user.setFriendCode("TestUser-AAAAAAAA");
            userRepository.save(user);
        }
    }

    @Test
    void upload() throws Exception {
        String json = """
                {
                    "activityName": "Laufen",
                    "activityDate": "2026-05-10",
                    "userId": "00000000-0000-0000-0000-000000000001",
                    "createdAt": "2026-05-10T10:00:00+02:00"
                }
                """;

        mvc.perform(put("/activities/{id}", ACTIVITY_ID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void doubleUpload() throws Exception {
        String json = """
                {
                    "activityName": "Laufen",
                    "activityDate": "2026-05-10",
                    "userId": "00000000-0000-0000-0000-000000000001",
                    "createdAt": "2026-05-10T10:00:00+02:00"
                }
                """;

        mvc.perform(put("/activities/{id}", ACTIVITY_ID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mvc.perform(put("/activities/{id}", ACTIVITY_ID_TWO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void sameIdTwice_createsThenUpdates() throws Exception {
        String json = """
                { 
                    "activityName": "Laufen", "activityDate": "2026-05-10",
                    "userId": "00000000-0000-0000-0000-000000000001",
                    "createdAt": "2026-05-10T10:00:00+02:00" 
                }
                """;

        mvc.perform(put("/activities/{id}", ACTIVITY_ID_ONE)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());

        mvc.perform(put("/activities/{id}", ACTIVITY_ID_ONE)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNoContent());
    }

    @Test
    void emptyUpload() throws Exception {
        String json = """
                {
                    "id": "",
                    "activityName": "",
                    "activityDate": ""
                }
                """;

        mvc.perform(put("/activities/{id}", ACTIVITY_ID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void emptyNameUpload() throws Exception {
        String json = """
                {
                    "activityName": "",
                    "activityDate": "2026-05-10",
                    "userId": "00000000-0000-0000-0000-000000000001",
                    "createdAt": "2026-05-10T10:00:00+02:00"
                }
                """;

        mvc.perform(put("/activities/{id}", ACTIVITY_ID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void emptyDateUpload() throws Exception {
        String json = """
                {
                    "activityName": "Laufen",
                    "activityDate": "",
                    "userId": "00000000-0000-0000-0000-000000000001",
                    "createdAt": "2026-05-10T10:00:00+02:00"
                }
                """;

        mvc.perform(put("/activities/{id}", ACTIVITY_ID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidDateUpload() throws Exception {
        String json = """
                {
                    "activityName": "Laufen",
                    "activityDate": "definitiv-kein-datum",
                    "userId": "00000000-0000-0000-0000-000000000001",
                    "createdAt": "2026-05-10T10:00:00+02:00"
                }
                """;

        mvc.perform(put("/activities/{id}", ACTIVITY_ID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}