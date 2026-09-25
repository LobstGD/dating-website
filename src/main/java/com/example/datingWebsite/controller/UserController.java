package com.example.datingWebsite.controller;

import com.example.datingWebsite.dto.UserRequest;
import com.example.datingWebsite.dto.UserResponse;
import com.example.datingWebsite.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> userResponse(
            @Valid @RequestBody UserRequest userRequest
    ) {
        UserResponse response = userService.register(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> verify(
            @Valid @RequestBody UserRequest userRequest
    ) {
        String token = userService.verify(userRequest);
        return ResponseEntity.ok(token);
    }
}
