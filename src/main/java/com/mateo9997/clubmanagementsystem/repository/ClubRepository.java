package com.mateo9997.clubmanagementsystem.repository;

import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubRepository extends JpaRepository<Club, Long> {
    Club findByUsername(String username);
    List<Club> findByIsPublic(boolean isPublic);
    Player findPlayerById(Long playerId);
}