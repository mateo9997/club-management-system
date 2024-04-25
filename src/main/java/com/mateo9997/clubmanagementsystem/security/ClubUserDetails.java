package com.mateo9997.clubmanagementsystem.security;

import com.mateo9997.clubmanagementsystem.model.Club;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class ClubUserDetails implements UserDetails {
    private Club club;

    public ClubUserDetails(Club club) {
        this.club = club;
    }

    public Club getClub() {
        return club;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return club.getPassword();
    }

    @Override
    public String getUsername() {
        return club.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

