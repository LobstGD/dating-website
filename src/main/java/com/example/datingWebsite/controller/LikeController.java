package com.example.datingWebsite.controller;

import com.example.datingWebsite.dto.ProfileResponse;
import com.example.datingWebsite.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
@AllArgsConstructor
@Tag(name = "Likes", description = "Управление лайками и матчами")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "Лайкнуть пользователя", description = "Ставит лайк пользователю. Если взаимный лайк — создает матч")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Лайк поставлен"),
            @ApiResponse(responseCode = "400", description = "Нельзя лайкнуть себя"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "Лайк уже существует")
    })
    @PostMapping("/{toUserId}")
    public ResponseEntity<Void> like(
            @PathVariable Long toUserId,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        likeService.like(toUserId, token);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Отозвать лайк", description = "Удаляет лайк. Если был матч — удаляет матч")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Лайк отозван"),
            @ApiResponse(responseCode = "404", description = "Лайк не найден")
    })
    @DeleteMapping("/{toUserId}")
    public ResponseEntity<Void> unlike(
            @PathVariable Long toUserId,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        likeService.unlike(toUserId, token);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Мои лайки", description = "Возвращает список лайкнутых пользователей с пагинацией")
    @ApiResponse(responseCode = "200", description = "Список лайков")
    @GetMapping("/my")
    public ResponseEntity<List<ProfileResponse>> getMyLikes(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(likeService.getMyLikes(token));
    }
}