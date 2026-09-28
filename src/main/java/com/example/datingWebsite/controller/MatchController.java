package com.example.datingWebsite.controller;

import com.example.datingWebsite.dto.ProfileResponse;
import com.example.datingWebsite.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@AllArgsConstructor
@Tag(name = "Matches", description = "Управление матчами (взаимные лайки)")
public class MatchController {

    private final MatchService matchService;

    @Operation(summary = "Мои матчи", description = "Возвращает список пользователей, с которыми есть взаимный лайк, с пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список матчей"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/my")
    public ResponseEntity<List<ProfileResponse>> getMyMatches(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(matchService.getMyMatches(token));
    }
}