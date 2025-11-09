package com.cinema.auth.service;

import com.cinema.auth.model.User;
import com.cinema.auth.repository.UserRepository;
import com.cinema.auth.security.JwtTokenProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String register(String email, String password, String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        User user = User.builder()
                .email(email)
                .password(encoder.encode(password))
                .role(role)
                .build();
        userRepository.save(user);
        return jwtTokenProvider.generateToken(email);
    }

    public String login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty() || !encoder.matches(password, user.get().getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return jwtTokenProvider.generateToken(email);
    }
}
