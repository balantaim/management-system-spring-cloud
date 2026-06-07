package com.martinatanasov.management.system.analytics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalyticsController.class)
@ActiveProfiles("local")
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/analytics/{userId}";
    private static final String VALID_USER_ID = "426c84c1-cdac-4908-9e9a-6be0d8e863f0";

    @Test
    void getAnalytics_validUserId_returnsOk() throws Exception {
        mockMvc.perform(get(BASE_URL, VALID_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(VALID_USER_ID))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAnalytics_differentUserId_returnsCorrectUserId() throws Exception {
        String userId = "AV";

        mockMvc.perform(get(BASE_URL, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void getAnalytics_missingUserId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/analytics/"))
                .andExpect(status().isNotFound());
    }
}