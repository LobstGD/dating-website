package com.example.datingWebsite.controller;

import com.example.datingWebsite.dto.ProfileRequest;
import com.example.datingWebsite.dto.ProfileResponse;
import com.example.datingWebsite.model.ProfileGender;
import com.example.datingWebsite.repository.ProfileSearchDao;
import com.example.datingWebsite.service.ProfileService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final ProfileSearchDao profileSearchDao;

    @PostMapping("/create")
    public ProfileResponse createProfile(
            @RequestBody ProfileRequest profileRequest,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");

        return profileService.createProfile(profileRequest, token);
    }

    @PutMapping("/update/{profile-id}")
    public ProfileResponse updateProfile(
            @PathVariable("profile-id") Long id,
            @RequestBody ProfileRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        return profileService.updateProfile(id, request, token);
    }

    @GetMapping("/find-all-profiles")
    public PagedModel<ProfileResponse> findAllProfiles(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return profileService.findAllProfiles(pageable);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedModel<ProfileResponse>> search(
            @RequestParam(required = false) ProfileGender gender,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String firstname,
            @PageableDefault(size = 20, sort = "age") Pageable pageable
    ) {
        return ResponseEntity.ok(
                profileSearchDao.search(gender, minAge, maxAge, city, firstname, pageable)
        );
    }
}