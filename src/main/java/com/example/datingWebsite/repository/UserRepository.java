package com.example.datingWebsite.repository;

import com.example.datingWebsite.model.Users;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByEmail(@NotEmpty(message = "Email can't be empty") String email);

    Users getByUsername(String username);
}
