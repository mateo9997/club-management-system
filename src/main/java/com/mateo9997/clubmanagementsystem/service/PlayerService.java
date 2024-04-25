package com.mateo9997.clubmanagementsystem.service;

import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.model.Player;
import com.mateo9997.clubmanagementsystem.repository.ClubRepository;
import com.mateo9997.clubmanagementsystem.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Transactional
    public Player createPlayer(Long clubId, Player player) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new NoSuchElementException("Club not found with id: " + clubId));

        player.setClub(club);
        return playerRepository.save(player);
    }

    @Transactional
    public List<Player> listPlayers(Long clubId) {
        return playerRepository.findByClubId(clubId);
    }

    @Transactional
    public Player updatePlayer(Long clubId, Long playerId, Player playerDetails) {
        Player player = playerRepository.findByIdAndClubId(playerId, clubId)
                .orElseThrow(() -> new NoSuchElementException("Player not found with id: " + playerId));

        player.setGivenName(playerDetails.getGivenName());
        player.setFamilyName(playerDetails.getFamilyName());
        player.setNationality(playerDetails.getNationality());
        player.setEmail(playerDetails.getEmail());
        player.setDateOfBirth(playerDetails.getDateOfBirth());

        return playerRepository.save(player);
    }

    @Transactional
    public void deletePlayer(Long clubId, Long playerId) {
        Player player = playerRepository.findByIdAndClubId(playerId, clubId)
                .orElseThrow(() -> new NoSuchElementException("Player not found with id: " + playerId));

        playerRepository.delete(player);
    }

    @Transactional
    public Player getPlayerDetails(Long clubId, Long playerId) {
        return playerRepository.findByIdAndClubId(playerId, clubId)
                .orElseThrow(() -> new NoSuchElementException("Player not found with id: " + playerId + " and clubId: " + clubId));
    }
}