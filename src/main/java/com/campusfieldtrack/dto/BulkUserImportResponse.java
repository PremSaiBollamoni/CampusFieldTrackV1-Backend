package com.campusfieldtrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkUserImportResponse {
    private int totalProcessed;
    private int successCount;
    private int failureCount;
    private String message;
}
