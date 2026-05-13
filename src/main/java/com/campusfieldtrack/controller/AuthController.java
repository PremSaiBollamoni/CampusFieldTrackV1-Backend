package com.campusfieldtrack.controller;

import com.campusfieldtrack.dto.ApiResponse;
import com.campusfieldtrack.dto.AuthRequest;
import com.campusfieldtrack.dto.AuthResponse;
import com.campusfieldtrack.dto.BulkUserImportRequest;
import com.campusfieldtrack.dto.BulkUserImportResponse;
import com.campusfieldtrack.dto.DeleteMultipleUsersRequest;
import com.campusfieldtrack.entity.User;
import com.campusfieldtrack.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/bulk-import")
    public ResponseEntity<ApiResponse<BulkUserImportResponse>> bulkImport(
            @Valid @RequestBody BulkUserImportRequest request,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getDetails() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized: Admin access required"));
            }

            BulkUserImportResponse response = authService.bulkImportUsers(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bulk import completed", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Bulk import failed: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Backend is running", "OK"));
    }

    @GetMapping("/test-admin")
    public ResponseEntity<String> testAdmin() {
        try {
            User user = authService.findByUsername("ADMIN-CFT");
            return ResponseEntity.ok("Admin user found: " + user.getUsername() + ", Email: " + user.getEmail() + ", Role: " + user.getRole());
        } catch (Exception e) {
            return ResponseEntity.ok("Admin user NOT found: " + e.getMessage());
        }
    }

    @PostMapping("/reset-admin-password")
    public ResponseEntity<String> resetAdminPassword() {
        try {
            authService.resetAdminPassword();
            return ResponseEntity.ok("Admin password reset successfully to: AdminCft#$Admin");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to reset password: " + e.getMessage());
        }
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String username, Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized"));
            }

            authService.deleteUser(username);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", username));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to delete user: " + e.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllUsers(Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized"));
            }

            List<Map<String, Object>> users = authService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success("Users retrieved", users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get users: " + e.getMessage()));
        }
    }

    @PostMapping("/users/delete-multiple")
    public ResponseEntity<ApiResponse<String>> deleteMultipleUsers(@RequestBody DeleteMultipleUsersRequest request, Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized"));
            }

            int deleted = authService.deleteMultipleUsers(request.getUsernames());
            return ResponseEntity.ok(ApiResponse.success("Deleted " + deleted + " users", String.valueOf(deleted)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to delete users: " + e.getMessage()));
        }
    }
}
