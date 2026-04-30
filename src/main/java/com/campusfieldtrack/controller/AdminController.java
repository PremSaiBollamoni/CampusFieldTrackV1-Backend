package com.campusfieldtrack.controller;

import com.campusfieldtrack.dto.ApiResponse;
import com.campusfieldtrack.entity.User;
import com.campusfieldtrack.repository.CheckpointRepository;
import com.campusfieldtrack.repository.RoutePointRepository;
import com.campusfieldtrack.repository.TrackingSessionRepository;
import com.campusfieldtrack.repository.UserRepository;
import com.campusfieldtrack.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrackingSessionRepository sessionRepository;

    @Autowired
    private RoutePointRepository routePointRepository;

    @Autowired
    private CheckpointRepository checkpointRepository;

    @Autowired
    private ExportService exportService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        long totalUsers = userRepository.count();
        
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        
        long sessionsToday = sessionRepository.countByStartTimeBetween(todayStart, todayEnd);
        double distanceToday = sessionRepository.sumDistanceByStartTimeBetween(todayStart, todayEnd);
        long stopsToday = sessionRepository.sumCheckpointsByStartTimeBetween(todayStart, todayEnd);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("sessionsToday", sessionsToday);
        stats.put("distanceToday", distanceToday);
        stats.put("stopsToday", stopsToday);
        
        return ResponseEntity.ok(ApiResponse.success("Stats retrieved", stats));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (User user : users) {
            // Skip admin users
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                continue;
            }
            
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("createdAt", user.getCreatedAt());
            userMap.put("sessionCount", sessionRepository.countByUserId(user.getId()));
            result.add(userMap);
        }
        
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", result));
    }

    @GetMapping("/sessions/all")
    public ResponseEntity<ApiResponse<Object>> getAllSessions() {
        List<Object> allSessions = new ArrayList<>();
        List<User> users = userRepository.findAll();
        
        for (User user : users) {
            List<com.campusfieldtrack.entity.TrackingSession> sessions = 
                sessionRepository.findByUserOrderByStartTimeDesc(user);
            
            for (com.campusfieldtrack.entity.TrackingSession session : sessions) {
                Map<String, Object> sessionMap = new HashMap<>();
                sessionMap.put("id", session.getSessionId());
                sessionMap.put("userId", session.getUser().getId());
                sessionMap.put("startTime", session.getStartTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
                sessionMap.put("endTime", session.getEndTime() != null ? 
                    session.getEndTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null);
                sessionMap.put("distanceKm", session.getDistanceKm());
                sessionMap.put("areaName", session.getAreaName());
                sessionMap.put("state", session.getState());
                
                // Add route points
                List<com.campusfieldtrack.entity.RoutePoint> routePoints = 
                    routePointRepository.findBySessionOrderBySequenceIndex(session);
                List<Object> routePointsList = new ArrayList<>();
                for (com.campusfieldtrack.entity.RoutePoint rp : routePoints) {
                    Map<String, Object> rpMap = new HashMap<>();
                    rpMap.put("lat", rp.getLatitude());
                    rpMap.put("lng", rp.getLongitude());
                    rpMap.put("alt", rp.getAltitude());
                    rpMap.put("speed", rp.getSpeedMs());
                    rpMap.put("accuracy", rp.getAccuracy());
                    rpMap.put("ts", rp.getTimestamp().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
                    routePointsList.add(rpMap);
                }
                sessionMap.put("routePoints", routePointsList);
                
                // Add checkpoints
                List<com.campusfieldtrack.entity.Checkpoint> checkpoints = 
                    checkpointRepository.findBySessionOrderBySequenceIndex(session);
                List<Object> checkpointsList = new ArrayList<>();
                for (com.campusfieldtrack.entity.Checkpoint cp : checkpoints) {
                    Map<String, Object> cpMap = new HashMap<>();
                    cpMap.put("lat", cp.getLatitude());
                    cpMap.put("lng", cp.getLongitude());
                    cpMap.put("arrivedAt", cp.getArrivedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
                    cpMap.put("departedAt", cp.getDepartedAt() != null ? 
                        cp.getDepartedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null);
                    cpMap.put("index", cp.getSequenceIndex());
                    checkpointsList.add(cpMap);
                }
                sessionMap.put("checkpoints", checkpointsList);
                
                allSessions.add(sessionMap);
            }
        }
        
        return ResponseEntity.ok(ApiResponse.success("All sessions retrieved", allSessions));
    }

    @GetMapping("/export/all")
    public ResponseEntity<byte[]> exportAllUsers() {
        try {
            byte[] excelData = exportService.exportAllUsers();
            
            String filename = "CampusFieldTrack_AllUsers_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export/user/{userId}")
    public ResponseEntity<byte[]> exportUserData(@PathVariable Long userId) {
        try {
            byte[] excelData = exportService.exportUserData(userId);
            
            String filename = "CampusFieldTrack_User_" + userId + "_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
