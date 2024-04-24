package com.mateo9997.clubmanagementsystem.service;

import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClubService {
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Club registerClub(Club club) {
        String encodedPassword = passwordEncoder.encode(club.getPassword());
        club.setPassword(encodedPassword);
        return clubRepository.save(club);
    }

    public Club getClubDetails(Long id) {
        return clubRepository.findById(id).orElseThrow(() -> new RuntimeException("Club not found"));
    }

    @Transactional
    public Club updateClub(Long clubId, Club clubDetails) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + clubId));

        club.setOfficialName(clubDetails.getOfficialName());
        club.setPopularName(clubDetails.getPopularName());
        club.setFederation(clubDetails.getFederation());
        club.setPublic(clubDetails.isPublic());
        // Update other fields

        return clubRepository.save(club);
    }

    public List<Club> findAllPublicClubs() {
        return clubRepository.findByIsPublic(true);
    }
}
