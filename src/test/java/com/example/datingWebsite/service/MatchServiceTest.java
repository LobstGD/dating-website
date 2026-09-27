package com.example.datingWebsite.service;

import com.example.datingWebsite.dto.ProfileResponse;
import com.example.datingWebsite.mapper.ProfileMapper;
import com.example.datingWebsite.model.Match;
import com.example.datingWebsite.model.Profile;
import com.example.datingWebsite.model.ProfileGender;
import com.example.datingWebsite.model.Users;
import com.example.datingWebsite.repository.MatchRepository;
import com.example.datingWebsite.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private MatchService matchService;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnMyMatches() {
        String token = "test-token";
        String email = "alex@mail.com";

        Users currentUser = new Users();
        currentUser.setId(1L);
        currentUser.setEmail(email);

        Users otherUser = new Users();
        otherUser.setId(2L);
        Profile otherProfile = new Profile();
        otherProfile.setId(1L);
        otherProfile.setUser(otherUser);
        otherProfile.setFirstname("Мария");
        otherUser.setProfile(otherProfile);

        Match match = new Match();
        match.setUser1(currentUser);
        match.setUser2(otherUser);

        ProfileResponse expectedResponse = new ProfileResponse(
                1L, "Мария", "Петрова", 23, ProfileGender.FEMALE, "СПб", "Био"
        );

        when(jwtService.extractUserName(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(currentUser));
        when(matchRepository.findByUser1OrUser2(currentUser, currentUser))
                .thenReturn(List.of(match));
        when(profileMapper.toResponse(otherProfile)).thenReturn(expectedResponse);

        List<ProfileResponse> result = matchService.getMyMatches(token);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Мария", result.get(0).firstName());
        verify(matchRepository).findByUser1OrUser2(currentUser, currentUser);
    }
}