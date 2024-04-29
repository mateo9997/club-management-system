package com.mateo9997.clubmanagementsystem.controller;

import com.mateo9997.clubmanagementsystem.dto.PlayerDTO;
import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.model.Player;
import com.mateo9997.clubmanagementsystem.security.ClubUserDetails;
import com.mateo9997.clubmanagementsystem.service.PlayerService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/club/{clubId}/player")
@Api(tags = "Player Management", description = "Manage players within a club")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @ApiOperation(value = "Create a new player", notes = "Create a new player within a specified club.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Player created successfully", response = PlayerDTO.class),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<?> createPlayer(@PathVariable Long clubId, @RequestBody Player player) {
        try {
            verifyClubAccess(clubId);
            Player newPlayer = playerService.createPlayer(clubId, player);
            PlayerDTO playerDTO = mapToDTO(newPlayer);
            return ResponseEntity.ok(playerDTO);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error occurred", e);
        }
    }

    @ApiOperation(value = "List all players", notes = "List all players within a specified club.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listPlayers(@PathVariable Long clubId) {
        try {
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
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error occurred", e);
        }
    }

    @ApiOperation(value = "Get player details", notes = "Get details of a specific player within a club.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Player details retrieved successfully", response = PlayerDTO.class),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "Player not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerDTO> getPlayerDetails(@PathVariable Long clubId, @PathVariable Long playerId) {
        try {
            verifyClubAccess(clubId);
            Player player = playerService.getPlayerDetails(clubId, playerId);
            PlayerDTO dto = mapToDTO(player);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found", e);
        }
    }

    @ApiOperation(value = "Update player details", notes = "Update details of a specific player within a club.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Player updated successfully", response = PlayerDTO.class),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "Player not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping("/{playerId}")
    public ResponseEntity<PlayerDTO> updatePlayer(@PathVariable Long clubId, @PathVariable Long playerId, @RequestBody Player playerDetails) {
        try {
            verifyClubAccess(clubId);
            Player updatedPlayer = playerService.updatePlayer(clubId, playerId, playerDetails);
            PlayerDTO dto = mapToDTO(updatedPlayer);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found", e);
        }
    }

    @ApiOperation(value = "Delete a player", notes = "Delete a specific player from a club.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Player deleted successfully"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "Player not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping("/{playerId}")
    public ResponseEntity<?> deletePlayer(@PathVariable Long clubId, @PathVariable Long playerId) {
        try {
            verifyClubAccess(clubId);
            playerService.deletePlayer(clubId, playerId);
            return ResponseEntity.ok("Player deleted successfully");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found", e);
        }
    }

    private void verifyClubAccess(Long clubId) throws AccessDeniedException {
        Club requestingClub = getAuthenticatedClub();
        if (!requestingClub.getId().equals(clubId)) {
            throw new AccessDeniedException("Access denied");
        }
    }

    private Club getAuthenticatedClub() throws IllegalStateException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof ClubUserDetails) {
            return ((ClubUserDetails) authentication.getPrincipal()).getClub();
        }
        throw new IllegalStateException("Authenticated user is not of type ClubUserDetails");
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
