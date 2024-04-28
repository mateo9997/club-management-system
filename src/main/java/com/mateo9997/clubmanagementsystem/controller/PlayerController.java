package com.mateo9997.clubmanagementsystem.controller;

import com.mateo9997.clubmanagementsystem.dto.PlayerDTO;
import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.model.Player;
import com.mateo9997.clubmanagementsystem.service.ClubService;
import com.mateo9997.clubmanagementsystem.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/club/{clubId}/player")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private ClubService clubService;

    // Methods with added security checks
    // Create a new player
    @PostMapping
    public ResponseEntity<?> createPlayer(@PathVariable Long clubId, @RequestBody Player player) {
        String username = getAuthenticatedUsername();
        if (username == null) {
            throw new AccessDeniedException("Access denied");
        }
        Player newPlayer = playerService.createPlayer(clubId, player);
        PlayerDTO playerDTO = mapToDTO(newPlayer);
        return ResponseEntity.ok(playerDTO);
    }

    // List all players in a club
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listPlayers(@PathVariable Long clubId) {
        String username = getAuthenticatedUsername();
        Player examplePlayer = playerService.findAnyPlayerByClubId(clubId);
        if (!(examplePlayer != null && examplePlayer.getClub().getUsername().equals(username))) {
            throw new AccessDeniedException("Access denied");
        }
        List<Player> players = playerService.listPlayers(clubId);
        List<Map<String, Object>> playerSummaries = players.stream()
                .map(player -> {
                    Map<String, Object> summary = new HashMap<>();
                    summary.put("id", player.getId());
                    summary.put("givenName", player.getGivenName());
                    summary.put("familyName", player.getFamilyName());
                    return summary;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(playerSummaries);
    }

    // Get details of a specific player
    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerDTO> getPlayerDetails(@PathVariable Long clubId, @PathVariable Long playerId) {
        if (!verifyClubAccess(playerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        Player player = playerService.getPlayerDetails(clubId, playerId);
        PlayerDTO dto = mapToDTO(player);
        return ResponseEntity.ok(dto);
    }

    // Update player details
    @PutMapping("/{playerId}")
    public ResponseEntity<PlayerDTO> updatePlayer(@PathVariable Long clubId, @PathVariable Long playerId, @RequestBody Player playerDetails) {
        if (!verifyClubAccess(playerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        Player updatedPlayer = playerService.updatePlayer(clubId, playerId, playerDetails);
        PlayerDTO dto = mapToDTO(updatedPlayer);
        return ResponseEntity.ok(dto);
    }

    // Delete a player
    @DeleteMapping("/{playerId}")
    public ResponseEntity<?> deletePlayer(@PathVariable Long clubId, @PathVariable Long playerId) {
        if (!verifyClubAccess(playerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        playerService.deletePlayer(clubId, playerId);
        return ResponseEntity.ok("Player deleted successfully");
    }

    // Utility method to verify access for the authenticated club using Player's club relationship
    private boolean verifyClubAccess(Long playerId) {
        String username = getAuthenticatedUsername();
        Player player = playerService.findPlayerById(playerId);
        Club club = clubService.findByUsername(username);
        return player.getClub().getId().equals(club.getId());
    }


    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        throw new IllegalStateException("Authenticated user is not recognized");
    }

    private PlayerDTO mapToDTO(Player player) {
        PlayerDTO dto = new PlayerDTO();
        dto.setId(player.getId());
        dto.setGivenName(player.getGivenName());
        dto.setFamilyName(player.getFamilyName());
        dto.setNationality(player.getNationality());
        dto.setEmail(player.getEmail());
        dto.setDateOfBirth(player.getDateOfBirth());
        return dto;
    }
}
