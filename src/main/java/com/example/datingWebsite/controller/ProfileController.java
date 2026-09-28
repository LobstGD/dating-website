package com.example.datingWebsite.controller;

import com.example.datingWebsite.dto.ProfileRequest;
import com.example.datingWebsite.dto.ProfileResponse;
import com.example.datingWebsite.model.Profile;
import com.example.datingWebsite.model.ProfileGender;
import com.example.datingWebsite.repository.ProfileSearchDao;
import com.example.datingWebsite.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
@AllArgsConstructor
@Tag(name = "Profiles", description = "Управление профилями пользователей")
public class ProfileController {

    private final ProfileService profileService;
    private final ProfileSearchDao profileSearchDao;

    @Operation(summary = "Создать профиль", description = "Создает профиль для текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Профиль создан"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "409", description = "Профиль уже существует")
    })
    @PostMapping
    public ResponseEntity<ProfileResponse> createProfile(
            @Valid @RequestBody ProfileRequest profileRequest,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        ProfileResponse response = profileService.createProfile(profileRequest, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Обновить профиль", description = "Обновляет профиль по ID. Только свой профиль")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Профиль обновлен"),
            @ApiResponse(responseCode = "403", description = "Нельзя обновить чужой профиль"),
            @ApiResponse(responseCode = "404", description = "Профиль не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProfileResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody ProfileRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(profileService.updateProfile(id, request, token));
    }

    @Operation(summary = "Все профили", description = "Возвращает список всех профилей с пагинацией")
    @ApiResponse(responseCode = "200", description = "Список профилей")
    @GetMapping
    public ResponseEntity<PagedModel<ProfileResponse>> findAllProfiles(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(profileService.findAllProfiles(pageable));
    }

    @Operation(summary = "Поиск профилей", description = "Поиск по фильтрам: пол, возраст, город, имя")
    @ApiResponse(responseCode = "200", description = "Результаты поиска")
    @GetMapping("/search")
    public ResponseEntity<PagedModel<ProfileResponse>> search(
            @RequestParam(required = false) ProfileGender gender,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String firstname,
            @PageableDefault(size = 20, sort = "age") Pageable pageable
    ) {
        return ResponseEntity.ok(
                profileSearchDao.search(gender, minAge, maxAge, city, firstname, pageable)
        );
    }

    @Operation(summary = "Профиль по ID", description = "Возвращает профиль по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Профиль найден"),
            @ApiResponse(responseCode = "404", description = "Профиль не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> findProfileById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(profileService.findProfileById(id));
    }
}