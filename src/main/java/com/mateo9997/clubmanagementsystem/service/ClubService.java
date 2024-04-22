package com.mateo9997.clubmanagementsystem.service;

import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClubService {
    @Autowired
    private ClubRepository clubRepository;

    public Club registerClub(Club club) {
        // pending to add logic to hash the password before saving
        return clubRepository.save(club);
    }

    public Club getClubDetails(Long id) {
        return clubRepository.findById(id).orElseThrow(() -> new RuntimeException("Club not found"));
    }
}