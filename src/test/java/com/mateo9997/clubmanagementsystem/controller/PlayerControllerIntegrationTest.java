package com.mateo9997.clubmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateo9997.clubmanagementsystem.dto.ClubDTO;
import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.model.Player;
import com.mateo9997.clubmanagementsystem.repository.ClubRepository;
import com.mateo9997.clubmanagementsystem.repository.PlayerRepository;
import com.mateo9997.clubmanagementsystem.service.ClubService;
import com.mateo9997.clubmanagementsystem.service.PlayerService;
import com.mateo9997.clubmanagementsystem.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
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

    private String authToken;
    private Long clubId;
    @Autowired
    private ClubService clubService;

    @Test
    public void testPlayerLifecycle() throws Exception {
        // Clean up the database before each test
        playerRepository.deleteAll();
        playerRepository.flush();
        clubRepository.deleteAll();
        clubRepository.flush();

        // Create a new club and authenticate
        Club club = new Club();
        club.setUsername("uniqueTestClub_");
        club.setPassword("password123");
        club.setOfficialName("Test Club Official");
        club.setPopularName("Test Club");
        club.setFederation("FIFA");
        club.setPublic(true);

        ClubDTO savedClub = clubService.registerClub(club);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(club.getUsername(), "password123")
        );
        authToken = jwtUtil.generateToken(authentication.getName());
        clubId = savedClub.getId();

        // Create a new player
        Player newPlayer = new Player();
        newPlayer.setGivenName("Jose");
        newPlayer.setFamilyName("Perez");
        newPlayer.setNationality("ESP");
        newPlayer.setEmail("jose.perez@example.com");
        Date birth = new Date(1985-8-20);
        newPlayer.setDateOfBirth(birth);

        // Create another player
        Player secondPlayer = new Player();
        secondPlayer.setGivenName("Maria");
        secondPlayer.setFamilyName("Lorca");
        secondPlayer.setNationality("ESP");
        secondPlayer.setEmail("maria.lorca@example.com");
        Date birth2 = new Date(1988-10-10);
        secondPlayer.setDateOfBirth(birth2);

        mockMvc.perform(post("/club/{clubId}/player", clubId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newPlayer))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.givenName").value("Jose"));

        mockMvc.perform(post("/club/{clubId}/player", clubId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(secondPlayer))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.givenName").value("Maria"));

        // List all players in a club
        mockMvc.perform(get("/club/{clubId}/player", clubId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))  // Verify that exactly two players are returned
                .andExpect(jsonPath("$.[0].email").exists())
                .andExpect(jsonPath("$.[1].email").exists());

        // Fetch the created player's ID
        Player createdPlayer = playerService.listPlayers(clubId).get(0);
        Long playerId = createdPlayer.getId();

        // Get player details
        mockMvc.perform(get("/club/{clubId}/player/{playerId}", clubId, playerId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jose.perez@example.com"));

        // Update player details
        createdPlayer.setEmail("new.jose.perez@example.com");
        mockMvc.perform(put("/club/{clubId}/player/{playerId}", clubId, playerId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createdPlayer))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new.jose.perez@example.com"));

        // Delete player
        mockMvc.perform(delete("/club/{clubId}/player/{playerId}", clubId, playerId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }
}

