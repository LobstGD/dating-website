package com.example.datingWebsite.repository;

import com.example.datingWebsite.model.Profile;
import com.example.datingWebsite.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Profile findByUser(Users user);
}
