package com.campusfieldtrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkUserImportRequest {
    private List<UserImportData> users;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserImportData {
        private String empId;
        private String name;
        private String email;
        private String designation;
        private String projectAssigned;
        private String employmentType;
    }
}
