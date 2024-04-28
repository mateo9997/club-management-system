package com.mateo9997.clubmanagementsystem.controller;

import com.mateo9997.clubmanagementsystem.dto.ClubDTO;
import com.mateo9997.clubmanagementsystem.dto.ClubPublicInfo;
import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.security.ClubUserDetails;
import com.mateo9997.clubmanagementsystem.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/club")
public class ClubController {
    @Autowired
    private ClubService clubService;

    @PostMapping
    public ClubDTO registerClub(@RequestBody Club club) {
        System.out.println("Received password: " + club.getPassword());
        return mapToDTO(clubService.registerClub(club));
    }

    // Get all public clubs
    @GetMapping
    public ResponseEntity<List<ClubPublicInfo>> listPublicClubs() {
        List<Club> clubs = clubService.findAllPublicClubs();
        // Convert to DTO to avoid sending sensitive data
        List<ClubPublicInfo> publicClubs = clubs.stream().map(club -> new ClubPublicInfo(
                club.getOfficialName(),
                club.getPopularName(),
                club.getFederation(),
                club.isPublic(),
                club.getPlayers().size() // Just the size but not the player details
        )).collect(Collectors.toList());

        return ResponseEntity.ok(publicClubs);
    }

    // Get details of a specific club
    @GetMapping("/{clubId}")
    public ResponseEntity<?> getClubDetails(@PathVariable Long clubId) {
        Club requestingClub = getAuthenticatedClub();
        if (!requestingClub.getId().equals(clubId)) {
            throw new AccessDeniedException("Access denied");
        }

        ClubDTO club = mapToDTO(clubService.getClubDetails(clubId));
        return ResponseEntity.ok(club);
    }

    // Update club details
    @PutMapping("/{clubId}")
    public ResponseEntity<?> updateClub(@PathVariable Long clubId, @RequestBody Club clubDetails) {
        Club requestingClub = getAuthenticatedClub();
        if (!requestingClub.getId().equals(clubId)) {
            throw new AccessDeniedException("Access denied");
        }

        ClubDTO updatedClub = mapToDTO(clubService.updateClub(clubId, clubDetails));
        return ResponseEntity.ok(updatedClub);
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