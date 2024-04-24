package com.mateo9997.clubmanagementsystem.controller;

import com.mateo9997.clubmanagementsystem.model.Player;
import com.mateo9997.clubmanagementsystem.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/club/{clubId}/player")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    // Create a new player
    @PostMapping
    public ResponseEntity<?> createPlayer(@PathVariable Long clubId, @RequestBody Player player) {
        try {
            Player newPlayer = playerService.createPlayer(clubId, player);
            return new ResponseEntity<>(newPlayer, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating player: " + e.getMessage());
        }
    }

    // List all players in a club
    @GetMapping
    public ResponseEntity<List<Player>> listPlayers(@PathVariable Long clubId) {
        try {
            List<Player> players = playerService.listPlayers(clubId);
            return ResponseEntity.ok(players);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Get details of a specific player
    @GetMapping("/{playerId}")
    public ResponseEntity<?> getPlayerDetails(@PathVariable Long clubId, @PathVariable Long playerId) {
        try {
            Player player = playerService.getPlayerDetails(clubId, playerId);
            return ResponseEntity.ok(player);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player not found");
        }
    }

    // Update player details
    @PutMapping("/{playerId}")
    public ResponseEntity<?> updatePlayer(@PathVariable Long clubId, @PathVariable Long playerId, @RequestBody Player playerDetails) {
        try {
            Player updatedPlayer = playerService.updatePlayer(clubId, playerId, playerDetails);
            return ResponseEntity.ok(updatedPlayer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error updating player: " + e.getMessage());
        }
    }

    // Delete a player
    @DeleteMapping("/{playerId}")
    public ResponseEntity<?> deletePlayer(@PathVariable Long clubId, @PathVariable Long playerId) {
        try {
            playerService.deletePlayer(clubId, playerId);
            return ResponseEntity.ok().body("Player deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error deleting player: " + e.getMessage());
        }
    }
}
