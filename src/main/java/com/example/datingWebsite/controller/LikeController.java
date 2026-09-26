package com.example.datingWebsite.controller;

import com.example.datingWebsite.dto.ProfileResponse;
import com.example.datingWebsite.service.LikeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
@AllArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{toUser-id}")
    public void like(
            @PathVariable("toUser-id") Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        likeService.like(id, token);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> unlike(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        likeService.unlike(userId, token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<ProfileResponse>> getMyLikes(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(likeService.getMyLikes(token));
    }
}