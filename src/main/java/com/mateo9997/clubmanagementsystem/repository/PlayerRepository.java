package com.mateo9997.clubmanagementsystem.repository;

import com.mateo9997.clubmanagementsystem.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByClubId(Long clubId);
    Optional<Player> findByIdAndClubId(Long playerId, Long clubId);
    Optional<Player> findTopByClubId(Long clubId);
}