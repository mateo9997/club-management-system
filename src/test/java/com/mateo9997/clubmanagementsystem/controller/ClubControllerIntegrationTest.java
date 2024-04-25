package com.mateo9997.clubmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.repository.ClubRepository;
import com.mateo9997.clubmanagementsystem.service.ClubService;
import com.mateo9997.clubmanagementsystem.util.JwtUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasProperty;
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

    private String authToken;


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

        mockMvc.perform(post("/club")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createClub)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("info@townfc.com"))
                .andExpect(jsonPath("$.officialName").value("Town Football Club"));


        String uniqueUsername = "uniqueTestClub_";
        Club newClub = new Club();
        newClub.setUsername(uniqueUsername);
        newClub.setPassword("password123");
        newClub.setOfficialName("Test Club Official");
        newClub.setPopularName("Test Club");
        newClub.setFederation("FIFA");
        newClub.setPublic(true);

        // Create a second club that is not public
        Club privateClub = new Club();
        privateClub.setUsername("uniquePrivateClub_");
        privateClub.setPassword("private123");
        privateClub.setOfficialName("Private Club Official");
        privateClub.setPopularName("Private Club");
        privateClub.setFederation("FIFA");
        privateClub.setPublic(false);

        // Create another public club
        Club anotherClub = new Club();
        anotherClub.setUsername("uniquePublicClub_");
        anotherClub.setPassword("public123");
        anotherClub.setOfficialName("Public Club Official");
        anotherClub.setPopularName("Public Club");
        anotherClub.setFederation("FIFA");
        anotherClub.setPublic(true);

        // Save the clubs
        clubService.registerClub(newClub);
        clubService.registerClub(privateClub);
        clubService.registerClub(anotherClub);

        // Authenticate and obtain JWT for created club
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(uniqueUsername, "password123")
        );
        authToken = jwtUtil.generateToken(authentication.getName());
        // Fetch the club to retrieve the actual clubId since it is auto-generated
        String username = "uniqueTestClub_";
        Club createdClub = clubService.findByUsername(username);
        long clubId = createdClub.getId();

        // Perform Get Club Details
        mockMvc.perform(get("/club/{clubId}", clubId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.officialName").value("Test Club Official"))
                .andExpect(jsonPath("$.password").doesNotExist()); // Ensure password is not included in the response

        // Prepare update details
        createdClub.setOfficialName("Updated Club Name");

        // Perform Update Club
        mockMvc.perform(put("/club/{clubId}", clubId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createdClub))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.officialName").value("Updated Club Name"));

        // Test listing all public clubs
        mockMvc.perform(get("/club")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", hasSize(3)))  // Expect 2 public clubs in the list
                .andExpect(jsonPath("$.[0].public").value(true))
                .andExpect(jsonPath("$.[1].public").value(true))
                .andExpect(jsonPath("$.[*].password").doesNotExist()); // Ensure passwords are not exposed

        // Optionally, check if the private club is not listed
        mockMvc.perform(get("/club")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(jsonPath("$.[*]", Matchers.not(Matchers.hasItem(hasProperty("username", Matchers.equalTo(privateClub.getUsername()))))));
    }

}
