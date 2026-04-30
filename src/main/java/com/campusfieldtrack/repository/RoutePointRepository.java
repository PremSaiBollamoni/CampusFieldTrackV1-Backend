package com.campusfieldtrack.repository;

import com.campusfieldtrack.entity.RoutePoint;
import com.campusfieldtrack.entity.TrackingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutePointRepository extends JpaRepository<RoutePoint, Long> {
    List<RoutePoint> findBySessionOrderBySequenceIndex(TrackingSession session);
    long countBySession(TrackingSession session);
}
