package com.mateo9997.clubmanagementsystem.controller;

import com.mateo9997.clubmanagementsystem.dto.ClubDTO;
import com.mateo9997.clubmanagementsystem.dto.ClubPublicInfo;
import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.security.ClubUserDetails;
import com.mateo9997.clubmanagementsystem.service.ClubService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/club")
@Api(tags = "Club Management", description = "Operations pertaining to clubs in the Club Management System")
public class ClubController {
    @Autowired
    private ClubService clubService;

    @ApiOperation(value = "Register a new club", notes = "Endpoint for registering a new club.")
    @ApiResponse(code = 200, message = "Club registered successfully")
    @PostMapping
    public ClubDTO registerClub(@RequestBody Club club) {
        return mapToDTO(clubService.registerClub(club));
    }

    @ApiOperation(value = "List all public clubs", notes = "Retrieve a list of all public clubs.")
    @ApiResponse(code = 200, message = "List retrieved successfully")
    @GetMapping
    public ResponseEntity<List<ClubPublicInfo>> listPublicClubs() {
        List<Club> clubs = clubService.findAllPublicClubs();
        List<ClubPublicInfo> publicClubs = clubs.stream().map(club -> new ClubPublicInfo(
                club.getOfficialName(),
                club.getPopularName(),
                club.getFederation(),
                club.isPublic(),
                club.getPlayers().size()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(publicClubs);
    }

    @ApiOperation(value = "Get club details", notes = "Retrieve details of a specific club.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Club details retrieved successfully", response = ClubDTO.class),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "Club not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/{clubId}")
    public ResponseEntity<?> getClubDetails(@PathVariable Long clubId) {
        try {
            Club requestingClub = getAuthenticatedClub();
            if (!requestingClub.getId().equals(clubId)) {
                throw new AccessDeniedException("Access denied");
            }
            ClubDTO club = mapToDTO(clubService.getClubDetails(clubId));
            return ResponseEntity.ok(club);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Club not found");
        }
    }

    @ApiOperation(value = "Update club details", notes = "Update details of a specific club.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Club updated successfully", response = ClubDTO.class),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "Club not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping("/{clubId}")
    public ResponseEntity<?> updateClub(@PathVariable Long clubId, @RequestBody Club clubDetails) {
        try {
            Club requestingClub = getAuthenticatedClub();
            if (!requestingClub.getId().equals(clubId)) {
                throw new AccessDeniedException("Access denied");
            }
            ClubDTO updatedClub = mapToDTO(clubService.updateClub(clubId, clubDetails));
            return ResponseEntity.ok(updatedClub);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Club not found");
        }
    }

    private Club getAuthenticatedClub() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof ClubUserDetails) {
            return ((ClubUserDetails) authentication.getPrincipal()).getClub();
        }
        throw new IllegalStateException("Authenticated user is not of type ClubUserDetails");
    }

    private ClubDTO mapToDTO(Club club) {
        ClubDTO dto = new ClubDTO();
        dto.setId(club.getId());
        dto.setUsername(club.getUsername());
        dto.setOfficialName(club.getOfficialName());
        dto.setPopularName(club.getPopularName());
        dto.setFederation(club.getFederation());
        dto.setPublic(club.isPublic());
        return dto;
    }
}