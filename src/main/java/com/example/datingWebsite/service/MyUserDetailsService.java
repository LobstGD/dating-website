package com.example.datingWebsite.service;

import com.example.datingWebsite.exception.UserNotFoundException;
import com.example.datingWebsite.model.UserPrincipal;
import com.example.datingWebsite.model.Users;
import com.example.datingWebsite.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Users user = repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user == null) {
            throw new UsernameNotFoundException("User not found!");
        }

        return new UserPrincipal(user);
    }
}
