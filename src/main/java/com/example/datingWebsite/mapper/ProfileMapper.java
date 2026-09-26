package com.example.datingWebsite.mapper;

import com.example.datingWebsite.dto.ProfileRequest;
import com.example.datingWebsite.dto.ProfileResponse;
import com.example.datingWebsite.model.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProfileMapper {

    public Profile toProfile(ProfileRequest request) {
        Profile profile = new Profile();
        profile.setFirstname(request.firstName());
        profile.setLastname(request.lastName());
        profile.setAge(request.age());
        profile.setGender(request.gender());
        profile.setCity(request.city());
        profile.setBio(request.bio());
        profile.setUpdatedAt(LocalDateTime.now());
        return profile;
    }

    public ProfileResponse toResponse(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getFirstname(),
                profile.getLastname(),
                profile.getAge(),
                profile.getGender(),
                profile.getCity(),
                profile.getBio()
        );
    }
}
