package com.example.datingWebsite.controller;

import com.example.datingWebsite.dto.UserRequest;
import com.example.datingWebsite.dto.UserResponse;
import com.example.datingWebsite.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Tag(name = "Authentication", description = "Регистрация и логин пользователя")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Регистрация пользователя", description = "Создает нового пользователя и возвращает его данные")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "409", description = "Email уже существует")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> userResponse(
            @Valid @RequestBody UserRequest userRequest
    ) {
        UserResponse response = userService.register(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Логин пользователя", description = "Аутентифицирует пользователя и возвращает JWT-токен")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный логин"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "401", description = "Неверный email или пароль")
    })
    @PostMapping("/login")
    public ResponseEntity<String> verify(
            @Valid @RequestBody UserRequest userRequest
    ) {
        String token = userService.verify(userRequest);
        return ResponseEntity.ok(token);
    }
}
