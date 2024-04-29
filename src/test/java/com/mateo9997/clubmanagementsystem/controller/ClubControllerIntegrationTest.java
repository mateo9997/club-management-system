package com.mateo9997.clubmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.repository.ClubRepository;
import com.mateo9997.clubmanagementsystem.service.ClubService;
import com.mateo9997.clubmanagementsystem.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ClubControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClubService clubService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    private String generateTokenWithClubId(String username, Long clubId) {
        return jwtUtil.generateToken(username, clubId);
    }

    @Test
    public void testClubLifecycle() throws Exception {
        // Clean up the database before each test
        clubRepository.deleteAll();
        clubRepository.flush();

        Club createClub = new Club();
        createClub.setUsername("info@townfc.com");
        createClub.setPassword("iTk19!n.");
        createClub.setOfficialName("Town Football Club");
        createClub.setPopularName("Town FC");
        createClub.setFederation("UEFA");
        createClub.setPublic(true);

        // Simulate club registration and immediately fetch its ID for authentication
        Club registeredClub = clubService.registerClub(createClub);
        Long registeredClubId = registeredClub.getId();
        String authToken = generateTokenWithClubId(registeredClub.getUsername(), registeredClubId);

        // Use the token for subsequent requests
        mockMvc.perform(post("/club")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createClub))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("info@townfc.com"))
                .andExpect(jsonPath("$.officialName").value("Town Football Club"));

        // Prepare and perform authenticated requests using the token
        mockMvc.perform(get("/club/{clubId}", registeredClubId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.officialName").value("Town Football Club"))
                .andExpect(jsonPath("$.password").doesNotExist()); // Ensure password is not included in the response

        createClub.setOfficialName("Updated Club Name");
        mockMvc.perform(put("/club/{clubId}", registeredClubId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createClub))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.officialName").value("Updated Club Name"));

        // Test listing all public clubs
        mockMvc.perform(get("/club")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", hasSize(1)))  // Only the registered public club should be listed
                .andExpect(jsonPath("$.[0].public").value(true))
                .andExpect(jsonPath("$.[*].password").doesNotExist()); // Ensure passwords are not exposed
    }
}

