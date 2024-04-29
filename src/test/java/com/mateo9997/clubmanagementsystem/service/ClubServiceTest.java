package com.mateo9997.clubmanagementsystem.service;

import com.mateo9997.clubmanagementsystem.dto.ClubDTO;
import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.repository.ClubRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClubServiceTest {

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClubService clubService;

    @Test
    public void registerClub_Success() {
        Club club = new Club();
        club.setPassword("rawPassword");

        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(clubRepository.save(any(Club.class))).thenReturn(club);

        ClubDTO registeredClub = clubService.registerClub(club);

        verify(clubRepository).save(club);
        verify(passwordEncoder).encode("rawPassword");
    }

    @Test
    public void getClubDetails_Success() {
        Club club = new Club();
        club.setId(1L);

        when(clubRepository.findById(1L)).thenReturn(Optional.of(club));

        ClubDTO foundClub = clubService.getClubDetails(1L);

        assertEquals(1L, foundClub.getId());
        verify(clubRepository).findById(1L);
    }

    @Test
    public void getClubDetails_NotFound() {
        when(clubRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> clubService.getClubDetails(1L));
    }

    @Test
    public void updateClub_Success() {
        Club existingClub = new Club();
        existingClub.setId(1L);
        existingClub.setOfficialName("Original Name");

        Club updatedClub = new Club();
        updatedClub.setOfficialName("Updated Name");

        when(clubRepository.findById(1L)).thenReturn(Optional.of(existingClub));
        when(clubRepository.save(existingClub)).thenReturn(existingClub);

        ClubDTO result = clubService.updateClub(1L, updatedClub);

        assertEquals("Updated Name", result.getOfficialName());
        verify(clubRepository).save(existingClub);
    }

    @Test
    public void updateClub_NotFound() {
        Club updatedInfo = new Club();
        when(clubRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> clubService.updateClub(1L, updatedInfo));
    }

    @Test
    public void findAllPublicClubs_Success() {
        List<Club> clubs = Arrays.asList(new Club(), new Club());
        when(clubRepository.findByIsPublic(true)).thenReturn(clubs);

        List<Club> result = clubService.findAllPublicClubs();

        assertEquals(2, result.size());
        verify(clubRepository).findByIsPublic(true);
    }


}
