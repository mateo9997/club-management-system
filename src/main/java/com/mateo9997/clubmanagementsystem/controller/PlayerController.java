package com.mateo9997.clubmanagementsystem.controller;

import com.mateo9997.clubmanagementsystem.dto.PlayerDTO;
import com.mateo9997.clubmanagementsystem.model.Player;
import com.mateo9997.clubmanagementsystem.security.ClubUserDetails;
import com.mateo9997.clubmanagementsystem.service.PlayerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(value = "Player Management", tags = {"Player Management"})
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @PostMapping
    @ApiOperation(value = "Create a new player for a club", response = PlayerDTO.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Player created successfully"),
            @ApiResponse(code = 403, message = "Access denied")
    })
    public ResponseEntity<?> createPlayer(@PathVariable Long clubId, @RequestBody Player player) {
        verifyClubAccess(clubId);
        Player newPlayer = playerService.createPlayer(clubId, player);
        PlayerDTO playerDTO = mapToDTO(newPlayer);
        return ResponseEntity.ok(playerDTO);
    }

    @GetMapping
    @ApiOperation(value = "List all players of a specific club", response = Map.class, responseContainer = "List")
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

    @GetMapping("/{playerId}")
    @ApiOperation(value = "Get details of a specific player", response = PlayerDTO.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Player details retrieved successfully"),
            @ApiResponse(code = 403, message = "Access denied")
    })
    public ResponseEntity<PlayerDTO> getPlayerDetails(@PathVariable Long clubId, @PathVariable Long playerId) {
        verifyClubAccess(clubId);
        Player player = playerService.getPlayerDetails(clubId, playerId);
        PlayerDTO dto = mapToDTO(player);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{playerId}")
    @ApiOperation(value = "Update details of a specific player", response = PlayerDTO.class)
    public ResponseEntity<PlayerDTO> updatePlayer(@PathVariable Long clubId, @PathVariable Long playerId, @RequestBody Player playerDetails) {
        verifyClubAccess(clubId);
        Player updatedPlayer = playerService.updatePlayer(clubId, playerId, playerDetails);
        PlayerDTO dto = mapToDTO(updatedPlayer);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{playerId}")
    @ApiOperation(value = "Delete a specific player from a club")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Player deleted successfully"),
            @ApiResponse(code = 403, message = "Access denied")
    })
    public ResponseEntity<?> deletePlayer(@PathVariable Long clubId, @PathVariable Long playerId) {
        verifyClubAccess(clubId);
        playerService.deletePlayer(clubId, playerId);
        return ResponseEntity.ok("Player deleted successfully");
    }

    private void verifyClubAccess(Long clubId) {
        Long authenticatedClubId = getAuthenticatedClubId();
        if (!authenticatedClubId.equals(clubId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    private Long getAuthenticatedClubId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof ClubUserDetails) {
            return ((ClubUserDetails) authentication.getPrincipal()).getClubId();
        }
        throw new IllegalStateException("Authenticated user is not of type CustomUserDetails");
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
