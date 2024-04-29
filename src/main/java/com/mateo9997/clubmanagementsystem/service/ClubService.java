package com.mateo9997.clubmanagementsystem.service;

import com.mateo9997.clubmanagementsystem.dto.ClubDTO;
import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClubService {
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ClubDTO registerClub(Club club) {
        String encodedPassword = passwordEncoder.encode(club.getPassword());
        club.setPassword(encodedPassword);
        clubRepository.save(club);
        return mapToDTO(club);
    }

    public ClubDTO getClubDetails(Long id) {
        Club club = clubRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Club not found"));
        return mapToDTO(club);
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


    @Transactional
    public ClubDTO updateClub(Long clubId, Club clubDetails) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new NoSuchElementException("Club not found with id: " + clubId));

        club.setOfficialName(clubDetails.getOfficialName());
        club.setPopularName(clubDetails.getPopularName());
        club.setFederation(clubDetails.getFederation());
        club.setPublic(clubDetails.isPublic());
        clubRepository.save(club);
        return mapToDTO(club);
    }

    public List<Club> findAllPublicClubs() {
        return clubRepository.findByIsPublic(true);
    }

    public Club findByUsername(String username) {
        return clubRepository.findByUsername(username);
    }
}
