package com.example.datingWebsite.service;

import com.example.datingWebsite.exception.LikeAlreadyExistsException;
import com.example.datingWebsite.mapper.ProfileMapper;
import com.example.datingWebsite.model.Like;
import com.example.datingWebsite.model.Match;
import com.example.datingWebsite.model.Users;
import com.example.datingWebsite.repository.LikeRepository;
import com.example.datingWebsite.repository.MatchRepository;
import com.example.datingWebsite.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MatchRepository matchRepository;
    @Mock
    private ProfileMapper profileMapper;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LikeService likeService;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldLikeUser() {
        String token = "test-token";

        Users fromUser = new Users();
        fromUser.setId(1L);
        fromUser.setEmail("alex@mail.ru");

        Users toUser = new Users();
        toUser.setId(2L);

        when(jwtService.extractUserName(token)).thenReturn("alex@mail.ru");
        when(userRepository.findByEmail("alex@mail.ru")).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(toUser));
        when(likeRepository.existsByFromUserAndToUser(fromUser, toUser)).thenReturn(false);

        likeService.like(2L, token);

        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void shouldThrowWhenLikingYourself() {
        Users user = new Users();
        user.setId(1L);

        when(jwtService.extractUserName(anyString())).thenReturn("alex@mail.ru");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> likeService.like(1L, "token"));
    }

    @Test
    void shouldThrowWhenAlreadyLiked() {
        Users fromUser = new Users();
        fromUser.setId(1L);
        Users toUser = new Users();
        toUser.setId(2L);

        when(jwtService.extractUserName(anyString())).thenReturn("alex@mail.ru");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(toUser));
        when(likeRepository.existsByFromUserAndToUser(fromUser, toUser)).thenReturn(true);

        assertThrows(LikeAlreadyExistsException.class, () -> likeService.like(2L, "token"));
    }

    @Test
    void shouldCreateMatchOnMutualLike() {
        Users fromUser = new Users();
        fromUser.setId(1L);
        Users toUser = new Users();
        toUser.setId(2L);

        when(jwtService.extractUserName(anyString())).thenReturn("alex@mail.ru");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(toUser));
        when(likeRepository.existsByFromUserAndToUser(fromUser, toUser)).thenReturn(false);
        when(likeRepository.existsByFromUserAndToUser(toUser, fromUser)).thenReturn(true); // ← взаимный

        likeService.like(2L, "token");

        verify(likeRepository).save(any(Like.class));
        verify(matchRepository).save(any(Match.class));
    }

    @Test
    void shouldUnlikeUser() {
        Users fromUser = new Users();
        fromUser.setId(1L);
        Users toUser = new Users();
        toUser.setId(2L);

        Like like = new Like();
        like.setFromUser(fromUser);
        like.setToUser(toUser);

        when(jwtService.extractUserName(anyString())).thenReturn("alex@mail.ru");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(toUser));
        when(likeRepository.findByFromUserAndToUser(fromUser, toUser)).thenReturn(Optional.of(like));

        likeService.unlike(2L, "token");

        verify(likeRepository).delete(like);
    }
}