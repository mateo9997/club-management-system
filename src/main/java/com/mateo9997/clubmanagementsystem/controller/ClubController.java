package com.mateo9997.clubmanagementsystem.controller;

import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/club")
public class ClubController {
    @Autowired
    private ClubService clubService;

    @PostMapping
    public Club registerClub(@RequestBody Club club) {
        return clubService.registerClub(club);
    }

    @GetMapping("/{id}")
    public Club getClubDetails(@PathVariable Long id) {
        return clubService.getClubDetails(id);
    }
}