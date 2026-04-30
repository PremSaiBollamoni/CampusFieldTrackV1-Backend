package com.campusfieldtrack.controller;

import com.campusfieldtrack.dto.ApiResponse;
import com.campusfieldtrack.dto.PasswordChangeRequest;
import com.campusfieldtrack.dto.UserRequest;
import com.campusfieldtrack.entity.User;
import com.campusfieldtrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<ApiResponse<User>> getUser(@RequestParam(required = false) Long id) {
        if (id != null) {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                User u = user.get();
                u.setPassword(null);
                return ResponseEntity.ok(ApiResponse.success("User found", u));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("User not found"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("User ID required"));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<User>> updateUser(@RequestParam Long id, @RequestBody UserRequest request) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("User not found"));
        }

        User user = userOpt.get();
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }

        User saved = userRepository.save(user);
        saved.setPassword(null);
        return ResponseEntity.ok(ApiResponse.success("User updated", saved));
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestParam Long id, @RequestBody PasswordChangeRequest request) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("User not found"));
        }

        User user = userOpt.get();
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Current password is incorrect"));
        }

        // Validate new password
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("New password must be at least 6 characters"));
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", "OK"));
    }
}