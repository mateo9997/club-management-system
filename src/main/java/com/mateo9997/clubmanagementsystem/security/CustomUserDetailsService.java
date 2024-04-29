package com.mateo9997.clubmanagementsystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.mateo9997.clubmanagementsystem.repository.ClubRepository;
import com.mateo9997.clubmanagementsystem.model.Club;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ClubRepository clubRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Club club = clubRepository.findByUsername(username);
        return new ClubUserDetails(club.getUsername(), club.getPassword(), club.getId());
    }
}


