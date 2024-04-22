package com.mateo9997.clubmanagementsystem.service;

import com.mateo9997.clubmanagementsystem.model.Player;
import com.mateo9997.clubmanagementsystem.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    public Player createPlayer(Player player) {
        return playerRepository.save(player);
    }
}