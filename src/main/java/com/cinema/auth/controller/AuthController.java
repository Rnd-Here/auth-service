package com.cinema.auth.controller;

import com.cinema.auth.model.User;
import com.cinema.auth.service.AuthService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader("Authorization") String bearerToken) {
        try {
            // Remove "Bearer " prefix
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                String idToken = bearerToken.substring(7);
                User user = authService.loginOrSignup(idToken);
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Header Format");
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token Verification Failed: " + e.getMessage());
        }
    }
}