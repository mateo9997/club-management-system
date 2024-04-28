package com.mateo9997.clubmanagementsystem.controller;

import com.mateo9997.clubmanagementsystem.dto.ClubDTO;
import com.mateo9997.clubmanagementsystem.dto.ClubPublicInfo;
import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/club")
public class ClubController {
    @Autowired
    private ClubService clubService;

    @PostMapping
    public ClubDTO registerClub(@RequestBody Club club) {
        return mapToDTO(clubService.registerClub(club));
    }

    // Get all public clubs
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

    // Get details of a specific club
    @GetMapping("/{clubId}")
    public ResponseEntity<?> getClubDetails(@PathVariable Long clubId) {
        if (!verifyClubAccess(clubId)) {
            throw new AccessDeniedException("Access denied");
        }
        Club club = clubService.getClubDetails(clubId); // This method should return the Club object
        ClubDTO clubDTO = mapToDTO(club);
        return ResponseEntity.ok(clubDTO);
    }

    // Update club details
    @PutMapping("/{clubId}")
    public ResponseEntity<?> updateClub(@PathVariable Long clubId, @RequestBody Club clubDetails) {
        if (!verifyClubAccess(clubId)) {
            throw new AccessDeniedException("Access denied");
        }
        Club updatedClub = clubService.updateClub(clubId, clubDetails);
        ClubDTO updatedClubDTO = mapToDTO(updatedClub);
        return ResponseEntity.ok(updatedClubDTO);
    }

    // Utility method to verify access for the authenticated club
    private boolean verifyClubAccess(Long clubId) {
        String username = getAuthenticatedUsername();
        Club club = clubService.findByUsername(username);
        return club.getId().equals(clubId);
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        throw new IllegalStateException("Authenticated user is not recognized");
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