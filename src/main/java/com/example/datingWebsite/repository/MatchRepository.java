package com.example.datingWebsite.repository;

import com.example.datingWebsite.model.Match;
import com.example.datingWebsite.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findByUser1AndUser2(Users user1, Users user2); // ← Добавь
    void deleteByUser1AndUser2(Users user1, Users user2);
    List<Match> findByUser1OrUser2(Users user1, Users user2);
}