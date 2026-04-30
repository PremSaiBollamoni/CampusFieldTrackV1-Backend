package com.campusfieldtrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionSyncRequest {
    @NotNull(message = "Session ID is required")
    private String id;

    @NotNull(message = "Start time is required")
    private Long startTime;

    private Long endTime;

    @NotNull(message = "Distance is required")
    private Double distanceKm;

    private String areaName;

    @NotNull(message = "State is required")
    private String state;

    @NotEmpty(message = "Route points cannot be empty")
    private List<RoutePointDto> routePoints;

    @NotEmpty(message = "Checkpoints cannot be empty")
    private List<CheckpointDto> checkpoints;
}
