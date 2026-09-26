package com.example.datingWebsite.repository;

import com.example.datingWebsite.model.Users;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByEmail(@NotEmpty(message = "Email can't be empty") String email);

    Optional<Users> findByEmail(String email);
}
