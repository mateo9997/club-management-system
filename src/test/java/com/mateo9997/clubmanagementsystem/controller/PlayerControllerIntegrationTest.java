package com.mateo9997.clubmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.model.Player;
import com.mateo9997.clubmanagementsystem.repository.ClubRepository;
import com.mateo9997.clubmanagementsystem.repository.PlayerRepository;
import com.mateo9997.clubmanagementsystem.service.ClubService;
import com.mateo9997.clubmanagementsystem.service.PlayerService;
import com.mateo9997.clubmanagementsystem.util.JwtUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PlayerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ClubService clubService;

    private String generateTokenWithClubId(String username, Long clubId) {
        return jwtUtil.generateToken(username, clubId);
    }

    @Test
    public void testPlayerLifecycle() throws Exception {
        // Setup - creating and registering a club, and generating a token
        Club club = new Club();
        club.setUsername("uniqueTestClub_");
        club.setPassword("password123");
        club.setOfficialName("Test Club Official");
        club.setPopularName("Test Club");
        club.setFederation("FIFA");
        club.setPublic(true);
        Club savedClub = clubService.registerClub(club);
        Long clubId = savedClub.getId();
        String authToken = generateTokenWithClubId(savedClub.getUsername(), clubId);

        // Creating players
        Player newPlayer = new Player();
        newPlayer.setGivenName("Jose");
        newPlayer.setFamilyName("Perez");
        newPlayer.setNationality("ESP");
        newPlayer.setEmail("jose.perez@example.com");
        Date birth = new Date(1985, 7, 20);  // Note: Month is 0-based in Date constructor
        newPlayer.setDateOfBirth(birth);

        Player secondPlayer = new Player();
        secondPlayer.setGivenName("Maria");
        secondPlayer.setFamilyName("Lorca");
        secondPlayer.setNationality("ESP");
        secondPlayer.setEmail("maria.lorca@example.com");
        Date birth2 = new Date(1988, 9, 10);
        secondPlayer.setDateOfBirth(birth2);

        // Testing player creation
        mockMvc.perform(post("/club/{clubId}/player", clubId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPlayer))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.givenName").value("Jose"));

        mockMvc.perform(post("/club/{clubId}/player", clubId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondPlayer))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.givenName").value("Maria"));

        // Testing listing players
        mockMvc.perform(get("/club/{clubId}/player", clubId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[*].id").isNotEmpty())
                .andExpect(jsonPath("$.[*].givenName").value(Matchers.containsInAnyOrder("Jose", "Maria")));

        // Fetch a player ID for further details
        Player createdPlayer = playerService.listPlayers(clubId).get(0);
        Long playerId = createdPlayer.getId();

        // Fetching and updating player details
        mockMvc.perform(get("/club/{clubId}/player/{playerId}", clubId, playerId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jose.perez@example.com"));

        createdPlayer.setEmail("new.jose.perez@example.com");
        mockMvc.perform(put("/club/{clubId}/player/{playerId}", clubId, playerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdPlayer))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new.jose.perez@example.com"));

        // Deleting a player
        mockMvc.perform(delete("/club/{clubId}/player/{playerId}", clubId, playerId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }
}
