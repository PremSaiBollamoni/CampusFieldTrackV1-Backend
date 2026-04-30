package com.campusfieldtrack.repository;

import com.campusfieldtrack.entity.Checkpoint;
import com.campusfieldtrack.entity.TrackingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckpointRepository extends JpaRepository<Checkpoint, Long> {
    List<Checkpoint> findBySessionOrderBySequenceIndex(TrackingSession session);
    long countBySession(TrackingSession session);
}
