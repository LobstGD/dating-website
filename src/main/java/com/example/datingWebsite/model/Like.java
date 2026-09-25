package com.example.datingWebsite.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "likes",
        uniqueConstraints =
        @UniqueConstraint(columnNames =
                {"from_user_id", "to_user_id"}))
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    private Users fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)
    private Users toUser;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}