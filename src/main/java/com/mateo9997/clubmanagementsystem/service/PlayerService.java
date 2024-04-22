package com.mateo9997.clubmanagementsystem.service;

import com.mateo9997.clubmanagementsystem.model.Player;
import com.mateo9997.clubmanagementsystem.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    public Player addPlayer(Player player) {
        return playerRepository.save(player);
    }

    public Optional<Player> updatePlayerDetails(Long id, Player updatedPlayer) {
        return playerRepository.findById(id).map(player -> {
            player.setGivenName(updatedPlayer.getGivenName());
            player.setFamilyName(updatedPlayer.getFamilyName());
            player.setNationality(updatedPlayer.getNationality());
            player.setEmail(updatedPlayer.getEmail());
            player.setDateOfBirth(updatedPlayer.getDateOfBirth());
            return playerRepository.save(player);
        });
    }

    public Optional<Player> findPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    public List<Player> listPlayersByClub(Long clubId) {
        return playerRepository.findByClubId(clubId);
    }

    public void deletePlayer(Long playerId) {
        playerRepository.deleteById(playerId);
    }
}
