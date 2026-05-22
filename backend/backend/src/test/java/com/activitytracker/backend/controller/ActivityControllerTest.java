package com.activitytracker.backend.controller;

import com.activitytracker.backend.repository.ActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MockMvc;

// .env has to be initialized in the startup config

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ActivityControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ActivityRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void upload() throws Exception {

        String json = """
                {
                    "id": "550e8400-e29b-41d4-a716-446655440000",
                    "activityName": "Laufen",
                    "activityDate": "2026-05-10"
                }
                """;

        mvc.perform(post("/activities/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void doubleUpload() throws Exception {

        String firstJson = """
                {
                    "id": "550e8400-e29b-41d4-a716-446655440001",
                    "activityName": "Laufen",
                    "activityDate": "2026-05-10"
                }
                """;
        String secondJson = """
                {
                    "id": "550e8400-e29b-41d4-a716-446655440001",
                    "activityName": "Laufen",
                    "activityDate": "2026-05-10"
                }
                """;

        mvc.perform(post("/activities/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstJson))
                .andExpect(status().isOk());

        mvc.perform(post("/activities/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondJson))
                .andExpect(status().isOk());
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

        mvc.perform(post("/activities/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void emptyIdUpload() throws Exception {

        String json = """
                {
                    "id": "",
                    "activityName": "Laufen",
                    "activityDate": "2026-05-10"
                }
                """;

        mvc.perform(post("/activities/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void emptyNameUpload() throws Exception {

        String json = """
                {
                    "id": "550e8400-e29b-41d4-a716-446655440002",
                    "activityName": "",
                    "activityDate": "2026-05-10"
                }
                """;

        mvc.perform(post("/activities/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void emptyDateUpload() throws Exception {

        String json = """
                {
                    "id": "550e8400-e29b-41d4-a716-446655440003",
                    "activityName": "Laufen",
                    "activityDate": ""
                }
                """;

        mvc.perform(post("/activities/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidIdUpload() throws Exception {

        String json = """
                {
                    "id": "invalid-id",
                    "activityName": "Laufen",
                    "activityDate": "2026-05-10"
                }
                """;

        mvc.perform(post("/activities/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidDateUpload() throws Exception {

        String json = """
                {
                    "id": "550e8400-e29b-41d4-a716-446655440004",
                    "activityName": "Laufen",
                    "activityDate": "definitiv-kein-datum"
                }
                """;

        mvc.perform(post("/activities/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }


}