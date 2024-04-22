package com.mateo9997.clubmanagementsystem.service;

import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClubService {

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Club registerClub(Club club) {
        club.setPassword(passwordEncoder.encode(club.getPassword())); // Encrypt password before saving
        return clubRepository.save(club);
    }

    public Optional<Club> updateClubDetails(Long id, Club updatedClub) {
        return clubRepository.findById(id).map(club -> {
            club.setOfficialName(updatedClub.getOfficialName());
            club.setPopularName(updatedClub.getPopularName());
            club.setFederation(updatedClub.getFederation());
            club.setPublic(updatedClub.isPublic());
            return clubRepository.save(club);
        });
    }

    public Optional<Club> findClubById(Long id) {
        return clubRepository.findById(id);
    }

    public List<Club> listAllPublicClubs() {
        return clubRepository.findByIsPublicTrue();
    }

    public void deleteClub(Long id) {
        clubRepository.deleteById(id);
    }
}
