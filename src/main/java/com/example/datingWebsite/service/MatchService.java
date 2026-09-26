package com.example.datingWebsite.service;

import com.example.datingWebsite.dto.ProfileResponse;
import com.example.datingWebsite.mapper.ProfileMapper;
import com.example.datingWebsite.model.Users;
import com.example.datingWebsite.repository.MatchRepository;
import com.example.datingWebsite.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final ProfileMapper profileMapper;

    public List<ProfileResponse> getMyMatches(String token) {
        String email = jwtService.extractUserName(token);
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        return matchRepository.findByUser1OrUser2(user, user).stream()
                .map(match -> {
                    Users other = match.getUser1().equals(user) ? match.getUser2() : match.getUser1();
                    return profileMapper.toResponse(other.getProfile());
                })
                .toList();
    }
}