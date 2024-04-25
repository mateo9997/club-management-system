package com.mateo9997.clubmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.repository.ClubRepository;
import com.mateo9997.clubmanagementsystem.service.ClubService;
import com.mateo9997.clubmanagementsystem.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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

        // Create a new unique club for each test run
        String uniqueUsername = "uniqueTestClub_";
        Club newClub = new Club();
        newClub.setUsername(uniqueUsername);
        newClub.setPassword("password123");
        newClub.setOfficialName("Test Club Official");
        newClub.setPopularName("Test Club");
        newClub.setFederation("FIFA");
        newClub.setPublic(true);

        // Save the new club
        clubService.registerClub(newClub);

        // Authenticate and obtain JWT for created club
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(uniqueUsername, "password123")
        );
        authToken = jwtUtil.generateToken(authentication.getName());
        // Fetch the club to retrieve the actual clubId since it is auto-generated
        String username = "uniqueTestClub_";
        Club createdClub = clubService.findByUsername(username);
        System.out.println("created Club: " + createdClub.getUsername());
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

    }

}
