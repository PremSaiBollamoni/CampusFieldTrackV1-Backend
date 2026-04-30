package com.campusfieldtrack.repository;

import com.campusfieldtrack.entity.TrackingSession;
import com.campusfieldtrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrackingSessionRepository extends JpaRepository<TrackingSession, Long> {
    Optional<TrackingSession> findBySessionId(String sessionId);
    Optional<TrackingSession> findBySessionIdAndUser(String sessionId, User user);
    List<TrackingSession> findByUserOrderByStartTimeDesc(User user);
    List<TrackingSession> findByUserAndStartTimeAfterOrderByStartTimeDesc(User user, LocalDateTime startTime);
    boolean existsBySessionId(String sessionId);
    
    // Admin queries
    long countByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(s.distanceKm), 0) FROM TrackingSession s WHERE s.startTime BETWEEN :start AND :end")
    double sumDistanceByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(s.checkpointsCount), 0) FROM TrackingSession s WHERE s.startTime BETWEEN :start AND :end")
    long sumCheckpointsByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    long countByUserId(Long userId);
}
