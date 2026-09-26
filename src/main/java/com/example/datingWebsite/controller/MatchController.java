package com.example.datingWebsite.controller;

import com.example.datingWebsite.dto.ProfileResponse;
import com.example.datingWebsite.service.MatchService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@AllArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/my")
    public ResponseEntity<List<ProfileResponse>> getMyMatches(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(matchService.getMyMatches(token));
    }
}