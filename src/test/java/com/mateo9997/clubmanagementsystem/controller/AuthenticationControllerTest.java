package com.mateo9997.clubmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateo9997.clubmanagementsystem.model.Club;
import com.mateo9997.clubmanagementsystem.security.CustomUserDetailsService;
import com.mateo9997.clubmanagementsystem.security.JwtAuthenticationEntryPoint;
import com.mateo9997.clubmanagementsystem.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

//    @Test
//    public void authenticateUser_Success() throws Exception {
//        // Given
//        Club club = new Club();
//        club.setUsername("user@test.com");
//        club.setPassword("password");
//        String token = "mockedToken";
//
//        Authentication auth = mock(Authentication.class);
//        UserDetails userDetails = mock(UserDetails.class);
//        when(userDetails.getUsername()).thenReturn(club.getUsername());
//        when(authenticationManager.authenticate(any())).thenReturn(auth);
//        when(auth.getPrincipal()).thenReturn(userDetails);
//        when(jwtUtil.generateToken(anyString())).thenReturn(token);
//
//        // When & Then
//        mockMvc.perform(post("/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(club)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.jwt").value(token));  // Ensure JSON path matches "jwt" field in AuthenticationResponse
//
//        verify(jwtUtil).generateToken(club.getUsername());
//    }
//
//    @Test
//    public void authenticateUser_InvalidCredentials() throws Exception {
//        // Given
//        Club club = new Club();
//        club.setUsername("user@test.com");
//        club.setPassword("wrongpassword");
//
//        doThrow(new BadCredentialsException("Invalid credentials")).when(authenticationManager).authenticate(any());
//
//        // When & Then
//        mockMvc.perform(post("/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(club)))
//                .andExpect(status().isUnauthorized());  // Expect HTTP 401 Unauthorized
//    }

}
