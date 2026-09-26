package com.example.datingWebsite.service;

import com.example.datingWebsite.dto.ProfileResponse;
import com.example.datingWebsite.exception.LikeAlreadyExistsException;
import com.example.datingWebsite.exception.LikeNotFoundException;
import com.example.datingWebsite.mapper.ProfileMapper;
import com.example.datingWebsite.model.Like;
import com.example.datingWebsite.model.Match;
import com.example.datingWebsite.model.Users;
import com.example.datingWebsite.repository.LikeRepository;
import com.example.datingWebsite.repository.MatchRepository;
import com.example.datingWebsite.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    private final ProfileMapper profileMapper;

    private final JwtService jwtService;

    @Transactional
    public void like(Long toUserId, String token) {
        String email = jwtService.extractUserName(token);

        Users fromUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        Users toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        if (likeRepository.existsByFromUserAndToUser(fromUser, toUser)) {
            throw new LikeAlreadyExistsException("You already liked this user");
        }

        if (fromUser.getId().equals(toUser.getId())) {
            throw new IllegalArgumentException("You can't like yourself");
        }

        Like like = new Like();
        like.setFromUser(fromUser);
        like.setToUser(toUser);
        like.setCreatedAt(LocalDateTime.now());
        likeRepository.save(like);

        if (likeRepository.existsByFromUserAndToUser(toUser, fromUser)) {
            Match match = new Match();
            match.setUser1(fromUser);
            match.setUser2(toUser);
            match.setCreatedAt(LocalDateTime.now());
            matchRepository.save(match);
        }
    }

    @Transactional
    public void unlike(Long toUserId, String token) {
        String email = jwtService.extractUserName(token);
        Users fromUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        Users toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        Like like = likeRepository.findByFromUserAndToUser(fromUser, toUser)
                .orElseThrow(() -> new LikeNotFoundException("Like not found!"));
        likeRepository.delete(like);

        matchRepository.findByUser1AndUser2(fromUser, toUser)
                .ifPresent(matchRepository::delete);
        matchRepository.findByUser1AndUser2(toUser, fromUser)
                .ifPresent(matchRepository::delete);
    }

    public List<ProfileResponse> getMyLikes(String token) {
        String email = jwtService.extractUserName(token);
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        return likeRepository.findByFromUserWithDetails(user).stream()
                .map(like -> profileMapper.toResponse(like.getToUser().getProfile()))
                .toList();
    }
}