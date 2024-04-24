package com.mateo9997.clubmanagementsystem.controller;

import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.model.Player;
import com.mateo9997.clubmanagementsystem.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<Player>> listPlayers(@PathVariable Long clubId) {
        verifyClubAccess(clubId);
        List<Player> players = playerService.listPlayers(clubId);
        return ResponseEntity.ok(players);
    }

    // Get details of a specific player
    @GetMapping("/{playerId}")
    public ResponseEntity<?> getPlayerDetails(@PathVariable Long clubId, @PathVariable Long playerId) {
        verifyClubAccess(clubId);
        Player player = playerService.getPlayerDetails(clubId, playerId);
        return ResponseEntity.ok(player);
    }

    // Update player details
    @PutMapping("/{playerId}")
    public ResponseEntity<?> updatePlayer(@PathVariable Long clubId, @PathVariable Long playerId, @RequestBody Player playerDetails) {
        verifyClubAccess(clubId);
        Player updatedPlayer = playerService.updatePlayer(clubId, playerId, playerDetails);
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
        return (Club) authentication.getPrincipal();
    }
}
