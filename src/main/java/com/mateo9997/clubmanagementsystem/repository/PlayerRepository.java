package com.mateo9997.clubmanagementsystem.repository;

import com.mateo9997.clubmanagementsystem.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByClubId(Long clubId);  // Find players by club ID

    Player findByEmail(String email);  // Find a player by unique email
}
