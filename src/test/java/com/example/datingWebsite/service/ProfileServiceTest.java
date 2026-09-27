package com.example.datingWebsite.service;

import com.example.datingWebsite.dto.ProfileRequest;
import com.example.datingWebsite.dto.ProfileResponse;
import com.example.datingWebsite.exception.ProfileAccessDeniedException;
import com.example.datingWebsite.exception.UserAlreadyExistsException;
import com.example.datingWebsite.exception.UserNotFoundException;
import com.example.datingWebsite.mapper.ProfileMapper;
import com.example.datingWebsite.model.Profile;
import com.example.datingWebsite.model.ProfileGender;
import com.example.datingWebsite.model.Users;
import com.example.datingWebsite.repository.ProfileRepository;
import com.example.datingWebsite.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private ProfileService profileService;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateProfile() {
        String token = "test-token";
        String email = "alex@mail.ru";

        ProfileRequest request = new ProfileRequest(
                "Алексей", "Иванов", 25, ProfileGender.MALE, "Москва", "Био"
        );

        Users user = new Users();
        user.setId(1L);
        user.setEmail(email);

        Profile profile = new Profile();
        profile.setId(1L);
        profile.setUser(user);
        profile.setFirstname("Алексей");
        profile.setLastname("Иванов");
        profile.setAge(25);
        profile.setGender(ProfileGender.MALE);
        profile.setCity("Москва");
        profile.setBio("Био");

        ProfileResponse expectedResponse = new ProfileResponse(
                1L, "Алексей", "Иванов", 25, ProfileGender.MALE, "Москва", "Био"
        );

        when(jwtService.extractUserName(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(profileRepository.existsByUser(user)).thenReturn(false);
        when(profileMapper.toProfile(request)).thenReturn(profile);
        when(profileRepository.save((ArgumentMatchers.any(Profile.class)))).thenReturn(profile);
        when(profileMapper.toResponse(profile)).thenReturn(expectedResponse);

        ProfileResponse response = profileService.createProfile(request, token);

        assertNotNull(response);
        assertEquals("Алексей", response.firstName());
        assertEquals(25, response.age());
        verify(profileRepository).save(ArgumentMatchers.any(Profile.class));
    }

    @Test
    void shouldThrowWhenProfileExists() {
        String token = "test-token";
        String email = "alex@mail.ru";

        ProfileRequest request = new ProfileRequest(
                "Алексей", "Иванов", 25, ProfileGender.MALE, "Москва", "Био"
        );

        Users user = new Users();
        user.setEmail(email);

        when(jwtService.extractUserName(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(profileRepository.existsByUser(user)).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> profileService.createProfile(request, token));

        verify(profileRepository, never()).save((ArgumentMatchers.any(Profile.class)));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        String token = "test-token";
        ProfileRequest request = new ProfileRequest(
                "Алексей", "Иванов", 25, ProfileGender.MALE, "Москва", "Био"
        );

        when(jwtService.extractUserName(token)).thenReturn("alex@mail.ru");
        when(userRepository.findByEmail("alex@mail.ru")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> profileService.createProfile(request, token));
    }

    @Test
    void shouldThrowWhenUpdatingOthersProfile() {
        String token = "test-token";
        String email = "alex@mail.ru";
        Long profileId = 1L;

        Users user = new Users();
        user.setId(1L);
        user.setEmail(email);

        Users otherUser = new Users();
        otherUser.setId(2L);

        Profile otherProfile = new Profile();
        otherProfile.setId(profileId);
        otherProfile.setUser(otherUser);

        ProfileRequest request = new ProfileRequest(
                "Алексей", "Иванов", 25, ProfileGender.MALE, "Москва", "Био"
        );

        when(jwtService.extractUserName(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(otherProfile));

        assertThrows(ProfileAccessDeniedException.class,
                () -> profileService.updateProfile(profileId, request, token));
    }
}