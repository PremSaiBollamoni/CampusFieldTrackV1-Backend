package com.campusfieldtrack.service;

import com.campusfieldtrack.dto.AuthRequest;
import com.campusfieldtrack.dto.AuthResponse;
import com.campusfieldtrack.dto.BulkUserImportRequest;
import com.campusfieldtrack.dto.BulkUserImportResponse;
import com.campusfieldtrack.entity.User;
import com.campusfieldtrack.repository.UserRepository;
import com.campusfieldtrack.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(AuthRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required for registration");
        }
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
            .empId(request.getEmpId())
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role("USER")
            .employmentType(request.getEmploymentType())
            .designation(request.getDesignation())
            .projectAssigned(request.getProjectAssigned())
            .build();

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        return AuthResponse.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .token(token)
            .role(user.getRole())
            .build();
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        System.out.println("🔐 Login attempt for: " + request.getUsername());
        System.out.println("🔐 Stored password hash: " + user.getPassword());
        System.out.println("🔐 Input password: " + request.getPassword());
        System.out.println("🔐 Password matches: " + passwordEncoder.matches(request.getPassword(), user.getPassword()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        return AuthResponse.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .token(token)
            .role(user.getRole())
            .build();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public void resetAdminPassword() {
        User admin = userRepository.findByUsername("ADMIN-CFT")
            .orElseThrow(() -> new IllegalArgumentException("Admin user not found"));
        
        String newPassword = "AdminCft#$Admin";
        admin.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(admin);
        
        System.out.println("✅ Admin password reset successfully");
        System.out.println("✅ New password hash: " + admin.getPassword());
    }

    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if ("ADMIN".equals(user.getRole())) {
            throw new IllegalArgumentException("Cannot delete admin user");
        }
        
        userRepository.delete(user);
    }

    public List<Map<String, Object>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("empId", user.getEmpId());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("role", user.getRole());
            userMap.put("employmentType", user.getEmploymentType());
            userMap.put("designation", user.getDesignation());
            userMap.put("projectAssigned", user.getProjectAssigned());
            userMap.put("createdAt", user.getCreatedAt());
            return userMap;
        }).collect(Collectors.toList());
    }

    @Transactional
    public int deleteMultipleUsers(List<String> usernames) {
        int deleted = 0;
        for (String username : usernames) {
            try {
                User user = userRepository.findByUsername(username).orElse(null);
                if (user != null && !"ADMIN".equals(user.getRole())) {
                    userRepository.delete(user);
                    deleted++;
                }
            } catch (Exception e) {
                // Continue with next user
            }
        }
        return deleted;
    }

    @Transactional
    public BulkUserImportResponse bulkImportUsers(BulkUserImportRequest request) {
        int successCount = 0;
        int failureCount = 0;

        for (BulkUserImportRequest.UserImportData userData : request.getUsers()) {
            try {
                if (userData.getEmpId() == null || userData.getEmpId().isBlank() ||
                    userData.getName() == null || userData.getName().isBlank() ||
                    userData.getEmail() == null || userData.getEmail().isBlank()) {
                    failureCount++;
                    continue;
                }

                // Use empId as username for login
                if (userRepository.existsByUsername(userData.getEmpId()) ||
                    userRepository.existsByEmail(userData.getEmail())) {
                    failureCount++;
                    continue;
                }

                String password = userData.getEmpId() + "@123";

                User user = User.builder()
                    .empId(userData.getEmpId())
                    .username(userData.getEmpId())  // Use empId as username
                    .email(userData.getEmail())
                    .password(passwordEncoder.encode(password))
                    .role("USER")
                    .employmentType(userData.getEmploymentType())
                    .designation(userData.getDesignation())
                    .projectAssigned(userData.getProjectAssigned())
                    .build();

                userRepository.save(user);
                successCount++;
            } catch (Exception e) {
                failureCount++;
            }
        }

        return BulkUserImportResponse.builder()
            .totalProcessed(request.getUsers().size())
            .successCount(successCount)
            .failureCount(failureCount)
            .message("Imported " + successCount + " users, " + failureCount + " failed")
            .build();
    }
}
