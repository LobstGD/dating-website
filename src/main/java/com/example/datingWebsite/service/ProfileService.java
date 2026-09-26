package com.example.datingWebsite.service;

import com.example.datingWebsite.dto.ProfileRequest;
import com.example.datingWebsite.dto.ProfileResponse;
import com.example.datingWebsite.exception.ProfileAccessDeniedException;
import com.example.datingWebsite.exception.ProfileNotFoundException;
import com.example.datingWebsite.exception.UserAlreadyExists;
import com.example.datingWebsite.exception.UserNotFoundException;
import com.example.datingWebsite.mapper.ProfileMapper;
import com.example.datingWebsite.model.Profile;
import com.example.datingWebsite.model.Users;
import com.example.datingWebsite.repository.ProfileRepository;
import com.example.datingWebsite.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ProfileMapper profileMapper;

    @Transactional
    public ProfileResponse createProfile(
            ProfileRequest request,
            String token
    ) {
        String email = jwtService.extractUserName(token);
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        if (profileRepository.existsByUser(user)) {
            throw new UserAlreadyExists("Profile already exists!");
        }

        Profile profile = profileMapper.toProfile(request);
        profile.setUser(user);
        profile.setUpdatedAt(LocalDateTime.now());

        Profile savedProfile = profileRepository.save(profile);
        return profileMapper.toResponse(savedProfile);
    }

    @Transactional
    public ProfileResponse updateProfile(
            Long id,
            ProfileRequest request,
            String token
    ) {
        String email = jwtService.extractUserName(token);
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found!"));

        if (!profile.getUser().getId().equals(user.getId())) {
            throw new ProfileAccessDeniedException("You can only update your own profile!");
        }

        profile.setFirstname(request.firstName());
        profile.setLastname(request.lastName());
        profile.setAge(request.age());
        profile.setGender(request.gender());
        profile.setCity(request.city());
        profile.setBio(request.bio());
        profile.setUpdatedAt(LocalDateTime.now());

        Profile savedProfile = profileRepository.save(profile);
        return profileMapper.toResponse(savedProfile);
    }

    public PagedModel<ProfileResponse> findAllProfiles(Pageable pageable) {
        Page<ProfileResponse> responsePage = profileRepository.findAll(pageable)
                .map(profileMapper::toResponse);
        return new PagedModel<>(responsePage);
    }
}
