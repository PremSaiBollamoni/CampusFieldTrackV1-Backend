package com.campusfieldtrack.service;

import com.campusfieldtrack.dto.CheckpointDto;
import com.campusfieldtrack.dto.RoutePointDto;
import com.campusfieldtrack.dto.SessionSyncRequest;
import com.campusfieldtrack.dto.SessionSyncResponse;
import com.campusfieldtrack.entity.Checkpoint;
import com.campusfieldtrack.entity.RoutePoint;
import com.campusfieldtrack.entity.TrackingSession;
import com.campusfieldtrack.entity.User;
import com.campusfieldtrack.repository.CheckpointRepository;
import com.campusfieldtrack.repository.RoutePointRepository;
import com.campusfieldtrack.repository.TrackingSessionRepository;
import com.campusfieldtrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class SessionSyncService {
    @Autowired
    private TrackingSessionRepository sessionRepository;

    @Autowired
    private RoutePointRepository routePointRepository;

    @Autowired
    private CheckpointRepository checkpointRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public SessionSyncResponse syncSession(SessionSyncRequest request, Long userId) {
        System.out.println("🔵 SessionSyncService.syncSession called for user: " + userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getRoutePoints() == null || request.getRoutePoints().isEmpty()) {
            throw new IllegalArgumentException("Route points cannot be empty");
        }

        if (request.getCheckpoints() == null || request.getCheckpoints().isEmpty()) {
            throw new IllegalArgumentException("Checkpoints cannot be empty");
        }

        System.out.println("📊 Processing session: " + request.getId());
        System.out.println("📊 Route points: " + request.getRoutePoints().size());
        System.out.println("📊 Checkpoints: " + request.getCheckpoints().size());

        TrackingSession session = sessionRepository.findBySessionIdAndUser(request.getId(), user)
            .orElse(null);

        if (session == null) {
            System.out.println("➕ Creating new session");
            session = TrackingSession.builder()
                .sessionId(request.getId())
                .user(user)
                .startTime(convertTimestamp(request.getStartTime()))
                .endTime(request.getEndTime() != null ? convertTimestamp(request.getEndTime()) : null)
                .distanceKm(request.getDistanceKm())
                .areaName(request.getAreaName())
                .state(request.getState())
                .routePointsCount(request.getRoutePoints().size())
                .checkpointsCount(request.getCheckpoints().size())
                .build();
            session = sessionRepository.save(session);
            System.out.println("✅ Session created with ID: " + session.getId());
        } else {
            System.out.println("🔄 Updating existing session");
            session.setEndTime(request.getEndTime() != null ? convertTimestamp(request.getEndTime()) : null);
            session.setDistanceKm(request.getDistanceKm());
            session.setAreaName(request.getAreaName());
            session.setState(request.getState());
            session.setRoutePointsCount(request.getRoutePoints().size());
            session.setCheckpointsCount(request.getCheckpoints().size());
            session = sessionRepository.save(session);
        }

        System.out.println("💾 Saving route points...");
        int routePointsSaved = saveRoutePoints(session, request.getRoutePoints());
        System.out.println("✅ Saved " + routePointsSaved + " route points");
        
        System.out.println("💾 Saving checkpoints...");
        int checkpointsSaved = saveCheckpoints(session, request.getCheckpoints());
        System.out.println("✅ Saved " + checkpointsSaved + " checkpoints");

        return SessionSyncResponse.builder()
            .sessionId(session.getId())
            .sessionUuid(session.getSessionId())
            .routePointsSaved(routePointsSaved)
            .checkpointsSaved(checkpointsSaved)
            .message("Session synced successfully")
            .build();
    }

    private int saveRoutePoints(TrackingSession session, List<RoutePointDto> routePoints) {
        List<RoutePoint> points = new ArrayList<>();

        for (int i = 0; i < routePoints.size(); i++) {
            RoutePointDto dto = routePoints.get(i);
            RoutePoint point = RoutePoint.builder()
                .session(session)
                .latitude(dto.getLat())
                .longitude(dto.getLng())
                .altitude(dto.getAlt() != null ? dto.getAlt() : 0.0)
                .speedMs(dto.getSpeed() != null ? dto.getSpeed() : 0.0)
                .accuracy(dto.getAccuracy() != null ? dto.getAccuracy() : 0.0)
                .timestamp(convertTimestamp(dto.getTs()))
                .sequenceIndex(i)
                .build();
            points.add(point);
        }

        routePointRepository.saveAll(points);
        return points.size();
    }

    private int saveCheckpoints(TrackingSession session, List<CheckpointDto> checkpoints) {
        List<Checkpoint> points = new ArrayList<>();

        for (CheckpointDto dto : checkpoints) {
            Long durationSeconds = null;
            if (dto.getDepartedAt() != null) {
                durationSeconds = (dto.getDepartedAt() - dto.getArrivedAt()) / 1000;
            }

            Checkpoint checkpoint = Checkpoint.builder()
                .session(session)
                .latitude(dto.getLat())
                .longitude(dto.getLng())
                .arrivedAt(convertTimestamp(dto.getArrivedAt()))
                .departedAt(dto.getDepartedAt() != null ? convertTimestamp(dto.getDepartedAt()) : null)
                .durationSeconds(durationSeconds)
                .sequenceIndex(dto.getIndex())
                .build();
            points.add(checkpoint);
        }

        checkpointRepository.saveAll(points);
        return points.size();
    }

    private LocalDateTime convertTimestamp(Long milliseconds) {
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(milliseconds),
            ZoneId.systemDefault()
        );
    }

    public List<Object> getUserSessions(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        List<TrackingSession> sessions = sessionRepository.findByUserOrderByStartTimeDesc(user);
        List<Object> result = new ArrayList<>();
        
        for (TrackingSession session : sessions) {
            java.util.Map<String, Object> sessionMap = new java.util.HashMap<>();
            sessionMap.put("id", session.getSessionId());
            sessionMap.put("userId", session.getUser().getId());
            sessionMap.put("startTime", session.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            sessionMap.put("endTime", session.getEndTime() != null ? 
                session.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : null);
            sessionMap.put("distanceKm", session.getDistanceKm());
            sessionMap.put("areaName", session.getAreaName());
            sessionMap.put("state", session.getState());
            
            List<RoutePoint> routePoints = routePointRepository.findBySessionOrderBySequenceIndex(session);
            List<Object> routePointsList = new ArrayList<>();
            for (RoutePoint rp : routePoints) {
                java.util.Map<String, Object> rpMap = new java.util.HashMap<>();
                rpMap.put("lat", rp.getLatitude());
                rpMap.put("lng", rp.getLongitude());
                rpMap.put("alt", rp.getAltitude());
                rpMap.put("speed", rp.getSpeedMs());
                rpMap.put("accuracy", rp.getAccuracy());
                rpMap.put("ts", rp.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                routePointsList.add(rpMap);
            }
            sessionMap.put("routePoints", routePointsList);
            
            List<Checkpoint> checkpoints = checkpointRepository.findBySessionOrderBySequenceIndex(session);
            List<Object> checkpointsList = new ArrayList<>();
            for (Checkpoint cp : checkpoints) {
                java.util.Map<String, Object> cpMap = new java.util.HashMap<>();
                cpMap.put("lat", cp.getLatitude());
                cpMap.put("lng", cp.getLongitude());
                cpMap.put("arrivedAt", cp.getArrivedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                cpMap.put("departedAt", cp.getDepartedAt() != null ? 
                    cp.getDepartedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : null);
                cpMap.put("index", cp.getSequenceIndex());
                checkpointsList.add(cpMap);
            }
            sessionMap.put("checkpoints", checkpointsList);
            
            result.add(sessionMap);
        }
        
        return result;
    }

    public Object getSessionById(String sessionId, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        TrackingSession session = sessionRepository.findBySessionIdAndUser(sessionId, user)
            .orElse(null);
        
        if (session == null) return null;
        
        java.util.Map<String, Object> sessionMap = new java.util.HashMap<>();
        sessionMap.put("id", session.getSessionId());
        sessionMap.put("userId", session.getUser().getId());
        sessionMap.put("startTime", session.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        sessionMap.put("endTime", session.getEndTime() != null ? 
            session.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : null);
        sessionMap.put("distanceKm", session.getDistanceKm());
        sessionMap.put("areaName", session.getAreaName());
        sessionMap.put("state", session.getState());
        
        List<RoutePoint> routePoints = routePointRepository.findBySessionOrderBySequenceIndex(session);
        List<Object> routePointsList = new ArrayList<>();
        for (RoutePoint rp : routePoints) {
            java.util.Map<String, Object> rpMap = new java.util.HashMap<>();
            rpMap.put("lat", rp.getLatitude());
            rpMap.put("lng", rp.getLongitude());
            rpMap.put("alt", rp.getAltitude());
            rpMap.put("speed", rp.getSpeedMs());
            rpMap.put("accuracy", rp.getAccuracy());
            rpMap.put("ts", rp.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            routePointsList.add(rpMap);
        }
        sessionMap.put("routePoints", routePointsList);
        
        List<Checkpoint> checkpoints = checkpointRepository.findBySessionOrderBySequenceIndex(session);
        List<Object> checkpointsList = new ArrayList<>();
        for (Checkpoint cp : checkpoints) {
            java.util.Map<String, Object> cpMap = new java.util.HashMap<>();
            cpMap.put("lat", cp.getLatitude());
            cpMap.put("lng", cp.getLongitude());
            cpMap.put("arrivedAt", cp.getArrivedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            cpMap.put("departedAt", cp.getDepartedAt() != null ? 
                cp.getDepartedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : null);
            cpMap.put("index", cp.getSequenceIndex());
            checkpointsList.add(cpMap);
        }
        sessionMap.put("checkpoints", checkpointsList);
        
        return sessionMap;
    }
}

