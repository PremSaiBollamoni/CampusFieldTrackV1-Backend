package com.campusfieldtrack.controller;

import com.campusfieldtrack.dto.ApiResponse;
import com.campusfieldtrack.dto.SessionSyncRequest;
import com.campusfieldtrack.dto.SessionSyncResponse;
import com.campusfieldtrack.service.SessionSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/sessions")
@CrossOrigin(origins = "*")
public class SessionController {
    @Autowired
    private SessionSyncService sessionSyncService;

    @PostMapping("/full-sync")
    public ResponseEntity<ApiResponse<SessionSyncResponse>> fullSync(
            @Valid @RequestBody SessionSyncRequest request,
            Authentication authentication) {
        try {
            System.out.println("🔵 Received session sync request");
            
            if (authentication == null || authentication.getDetails() == null) {
                System.out.println("❌ Unauthorized: No valid token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized: No valid token"));
            }

            Long userId = (Long) authentication.getDetails();
            System.out.println("✅ User ID: " + userId);
            System.out.println("📊 Route points: " + (request.getRoutePoints() != null ? request.getRoutePoints().size() : 0));
            System.out.println("📊 Checkpoints: " + (request.getCheckpoints() != null ? request.getCheckpoints().size() : 0));
            
            SessionSyncResponse response = sessionSyncService.syncSession(request, userId);
            
            System.out.println("✅ Session synced successfully: " + response.getSessionUuid());
            System.out.println("💾 Saved " + response.getRoutePointsSaved() + " route points, " + response.getCheckpointsSaved() + " checkpoints");
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Session synced successfully", response));
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Bad request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            System.out.println("❌ Internal error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to sync session: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllSessions(Authentication authentication) {
        try {
            System.out.println("🔵 GET /sessions called");
            
            if (authentication == null || authentication.getDetails() == null) {
                System.out.println("❌ Unauthorized: No valid token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized: No valid token"));
            }

            Long userId = (Long) authentication.getDetails();
            System.out.println("✅ User ID: " + userId);
            
            var sessions = sessionSyncService.getUserSessions(userId);
            System.out.println("✅ Retrieved " + sessions.size() + " sessions");
            
            return ResponseEntity.ok(ApiResponse.success("Sessions retrieved", sessions));
        } catch (Exception e) {
            System.out.println("❌ Internal error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve sessions: " + e.getMessage()));
        }
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<Object>> getSessionById(
            @PathVariable String sessionId,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getDetails() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized: No valid token"));
            }

            Long userId = (Long) authentication.getDetails();
            var session = sessionSyncService.getSessionById(sessionId, userId);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Session not found"));
            }
            return ResponseEntity.ok(ApiResponse.success("Session retrieved", session));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve session: " + e.getMessage()));
        }
    }
}
