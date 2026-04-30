package com.campusfieldtrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionSyncResponse {
    private Long sessionId;
    private String sessionUuid;
    private Integer routePointsSaved;
    private Integer checkpointsSaved;
    private String message;
}
