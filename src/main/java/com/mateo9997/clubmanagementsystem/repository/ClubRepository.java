package com.mateo9997.clubmanagementsystem.repository;

import com.mateo9997.clubmanagementsystem.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    Club findByUsername(String username);  // Find a club by username
}
