package com.mateo9997.clubmanagementsystem.security;

import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.repository.ClubRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ClubRepository clubRepository;  // Your repository to access user data

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Club club = clubRepository.findByUsername(username);
        return new CustomUserDetails(club.getUsername(), club.getPassword());
    }
}

