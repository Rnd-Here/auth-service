package com.cinema.auth.service;

import com.cinema.auth.model.User;
import com.cinema.auth.repository.UserRepository;
import com.cinema.auth.security.JwtTokenProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User loginOrSignup(String idToken) throws FirebaseAuthException {
        // 1. Verify Token with Firebase
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String uid = decodedToken.getUid();

        // 2. Check if user exists in Postgres
        Optional<User> existingUser = userRepository.findByFirebaseUid(uid);

        if (existingUser.isPresent()) {
            // --- LOGIN FLOW ---
            User user = existingUser.get();
            user.setLastLoginAt(LocalDateTime.now());
            return userRepository.save(user);
        } else {
            // --- SIGNUP FLOW ---
            User newUser = new User();
            newUser.setFirebaseUid(uid);
            newUser.setEmail(decodedToken.getEmail());
            newUser.setDisplayName(decodedToken.getName());
            newUser.setPhotoUrl(decodedToken.getPicture());
            newUser.setEmailVerified(decodedToken.isEmailVerified());

            // Note: Phone number is not always in the token claims directly
            // unless using phone auth, sometimes needs a separate lookup if critical.
            // newUser.setPhoneNumber(decodedToken.getClaims().get("phone_number"));

            newUser.setLastLoginAt(LocalDateTime.now());
            return userRepository.save(newUser);
        }
    }
}