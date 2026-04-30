package com.campusfieldtrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckpointDto {
    @NotNull(message = "Latitude is required")
    private Double lat;

    @NotNull(message = "Longitude is required")
    private Double lng;

    @NotNull(message = "Arrived at timestamp is required")
    private Long arrivedAt;

    private Long departedAt;

    @NotNull(message = "Index is required")
    private Integer index;
}
