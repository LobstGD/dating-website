package com.example.datingWebsite.service;

import com.example.datingWebsite.dto.UserRequest;
import com.example.datingWebsite.dto.UserResponse;
import com.example.datingWebsite.exception.EmailAlreadyExistsException;
import com.example.datingWebsite.model.Users;
import com.example.datingWebsite.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterUser() {
        UserRequest request = new UserRequest("alex@mail.ru", "password123");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(ArgumentMatchers.<Users>any()))
                .thenAnswer(i -> i.getArgument(0, Users.class));

        UserResponse response = userService.register(request);

        assertNotNull(response);
        assertEquals("alex@mail.ru", response.email());
        verify(userRepository).save(ArgumentMatchers.<Users>any());
    }

    @Test
    void shouldThrowWhenEmailExists() {
        UserRequest request = new UserRequest("alex@mail.ru", "password123");
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.register(request));
    }
}