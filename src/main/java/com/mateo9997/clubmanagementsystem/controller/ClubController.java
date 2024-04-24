package com.mateo9997.clubmanagementsystem.controller;

import com.mateo9997.clubmanagementsystem.dto.ClubPublicInfo;
import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public Club registerClub(@RequestBody Club club) {
        return clubService.registerClub(club);
    }

    @GetMapping("/{clubId}")
    public ResponseEntity<?> getClubDetails(@PathVariable Long clubId) {
        try {
            Club club = clubService.getClubDetails(clubId);
            return ResponseEntity.ok(club);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Club not found");
        }
    }

    @PutMapping("/{clubId}")
    public ResponseEntity<?> updateClub(@PathVariable Long clubId, @RequestBody Club clubDetails) {
        try {
            Club updatedClub = clubService.updateClub(clubId, clubDetails);
            return ResponseEntity.ok(updatedClub);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Club not found");
        }
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

}