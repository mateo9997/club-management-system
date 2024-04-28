package com.mateo9997.clubmanagementsystem.controller;

import com.mateo9997.clubmanagementsystem.dto.PlayerDTO;
import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.model.Player;
import com.mateo9997.clubmanagementsystem.security.ClubUserDetails;
import com.mateo9997.clubmanagementsystem.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
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

    // Methods with added security checks
    // Create a new player
    @PostMapping
    public ResponseEntity<?> createPlayer(@PathVariable Long clubId, @RequestBody Player player) {
        verifyClubAccess(clubId);
        Player newPlayer = playerService.createPlayer(clubId, player);
        return ResponseEntity.ok(newPlayer);
    }

    // List all players in a club
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listPlayers(@PathVariable Long clubId) {
        verifyClubAccess(clubId);
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
        verifyClubAccess(clubId);
        PlayerDTO player = playerService.getPlayerDetails(clubId, playerId);
        return ResponseEntity.ok(player);
    }

    // Update player details
    @PutMapping("/{playerId}")
    public ResponseEntity<PlayerDTO> updatePlayer(@PathVariable Long clubId, @PathVariable Long playerId, @RequestBody Player playerDetails) {
        verifyClubAccess(clubId);
        PlayerDTO updatedPlayer = playerService.updatePlayer(clubId, playerId, playerDetails);
        return ResponseEntity.ok(updatedPlayer);
    }

    // Delete a player
    @DeleteMapping("/{playerId}")
    public ResponseEntity<?> deletePlayer(@PathVariable Long clubId, @PathVariable Long playerId) {
        verifyClubAccess(clubId);
        playerService.deletePlayer(clubId, playerId);
        return ResponseEntity.ok("Player deleted successfully");
    }

    private void verifyClubAccess(Long clubId) {
        Club requestingClub = getAuthenticatedClub();
        if (!requestingClub.getId().equals(clubId)) {
            throw new AccessDeniedException("Access denied");
        }
    }

    private Club getAuthenticatedClub() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof ClubUserDetails) {
            return ((ClubUserDetails) authentication.getPrincipal()).getClub();
        }
        throw new IllegalStateException("Authenticated user is not of type ClubUserDetails");
    }

}
