package com.mateo9997.clubmanagementsystem.repository;

import com.mateo9997.clubmanagementsystem.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}