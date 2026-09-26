package com.example.datingWebsite.repository;

import com.example.datingWebsite.model.Like;
import com.example.datingWebsite.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByFromUserAndToUser(Users fromUser, Users toUser);
    Optional<Like> findByFromUserAndToUser(Users fromUser, Users toUser);
    List<Like> findByFromUser(Users fromUser);

    @Query("SELECT l FROM Like l " +
            "JOIN FETCH l.toUser u " +
            "JOIN FETCH u.profile " +
            "WHERE l.fromUser = :fromUser")
    List<Like> findByFromUserWithDetails(@Param("fromUser") Users fromUser);
}
