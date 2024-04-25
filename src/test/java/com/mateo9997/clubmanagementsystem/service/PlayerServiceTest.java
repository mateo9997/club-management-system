package com.mateo9997.clubmanagementsystem.service;

import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.model.Player;
import com.mateo9997.clubmanagementsystem.repository.ClubRepository;
import com.mateo9997.clubmanagementsystem.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private ClubRepository clubRepository;

    @InjectMocks
    private PlayerService playerService;

    @Test
    public void createPlayer_Success() {
        Club club = new Club();
        club.setId(1L);
        Player player = new Player();
        player.setGivenName("John");

        when(clubRepository.findById(1L)).thenReturn(Optional.of(club));
        when(playerRepository.save(player)).thenReturn(player);

        Player createdPlayer = playerService.createPlayer(1L, player);

        assertNotNull(createdPlayer);
        assertEquals("John", createdPlayer.getGivenName());
        verify(playerRepository).save(player);
    }

    @Test
    public void createPlayer_ClubNotFound() {
        Player player = new Player();
        when(clubRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> playerService.createPlayer(1L, player));
    }

    @Test
    public void listPlayers_Success() {
        List<Player> players = Arrays.asList(new Player(), new Player());
        when(playerRepository.findByClubId(1L)).thenReturn(players);

        List<Player> result = playerService.listPlayers(1L);

        assertEquals(2, result.size());
        verify(playerRepository).findByClubId(1L);
    }

    @Test
    public void updatePlayer_Success() {
        Player existingPlayer = new Player();
        existingPlayer.setId(1L);
        existingPlayer.setGivenName("John");

        Player newDetails = new Player();
        newDetails.setGivenName("Johnny");

        when(playerRepository.findByIdAndClubId(1L, 1L)).thenReturn(Optional.of(existingPlayer));
        when(playerRepository.save(existingPlayer)).thenReturn(existingPlayer);

        Player updatedPlayer = playerService.updatePlayer(1L, 1L, newDetails);

        assertEquals("Johnny", updatedPlayer.getGivenName());
        verify(playerRepository).save(existingPlayer);
    }

    @Test
    public void updatePlayer_NotFound() {
        when(playerRepository.findByIdAndClubId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> playerService.updatePlayer(1L, 1L, new Player()));
    }

    @Test
    public void deletePlayer_Success() {
        Player player = new Player();
        player.setId(1L);

        when(playerRepository.findByIdAndClubId(1L, 1L)).thenReturn(Optional.of(player));
        doNothing().when(playerRepository).delete(player);

        assertDoesNotThrow(() -> playerService.deletePlayer(1L, 1L));
        verify(playerRepository).delete(player);
    }

    @Test
    public void deletePlayer_NotFound() {
        when(playerRepository.findByIdAndClubId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> playerService.deletePlayer(1L, 1L));
    }

    @Test
    public void getPlayerDetails_Success() {
        Player player = new Player();
        player.setId(1L);

        when(playerRepository.findByIdAndClubId(1L, 1L)).thenReturn(Optional.of(player));

        Player result = playerService.getPlayerDetails(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(playerRepository).findByIdAndClubId(1L, 1L);
    }

    @Test
    public void getPlayerDetails_NotFound() {
        when(playerRepository.findByIdAndClubId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> playerService.getPlayerDetails(1L, 1L));
    }


}