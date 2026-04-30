package com.campusfieldtrack.service;

import com.campusfieldtrack.entity.Checkpoint;
import com.campusfieldtrack.entity.RoutePoint;
import com.campusfieldtrack.entity.TrackingSession;
import com.campusfieldtrack.entity.User;
import com.campusfieldtrack.repository.CheckpointRepository;
import com.campusfieldtrack.repository.RoutePointRepository;
import com.campusfieldtrack.repository.TrackingSessionRepository;
import com.campusfieldtrack.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrackingSessionRepository sessionRepository;

    @Autowired
    private RoutePointRepository routePointRepository;

    @Autowired
    private CheckpointRepository checkpointRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public byte[] exportAllUsers() throws IOException {
        List<User> users = userRepository.findAll();
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            
            // Create Users Summary Sheet
            Sheet summarySheet = workbook.createSheet("Users Summary");
            createUsersSummarySheet(summarySheet, users, headerStyle, dataStyle, dateStyle);
            
            // Create individual sheets for each user
            for (User user : users) {
                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    continue; // Skip admin users
                }
                
                String sheetName = sanitizeSheetName(user.getUsername());
                Sheet userSheet = workbook.createSheet(sheetName);
                createUserDetailSheet(userSheet, user, headerStyle, dataStyle, dateStyle);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportUserData(Long userId) throws IOException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            
            Sheet userSheet = workbook.createSheet(user.getUsername());
            createUserDetailSheet(userSheet, user, headerStyle, dataStyle, dateStyle);
            
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void createUsersSummarySheet(Sheet sheet, List<User> users, CellStyle headerStyle, CellStyle dataStyle, CellStyle dateStyle) {
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"User ID", "Username", "Email", "Total Sessions", "Total Distance (km)", "Total Stops", "Created At"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Fill data
        int rowNum = 1;
        for (User user : users) {
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                continue;
            }
            
            Row row = sheet.createRow(rowNum++);
            
            List<TrackingSession> sessions = sessionRepository.findByUserOrderByStartTimeDesc(user);
            double totalDistance = sessions.stream().mapToDouble(TrackingSession::getDistanceKm).sum();
            int totalStops = sessions.stream().mapToInt(TrackingSession::getCheckpointsCount).sum();
            
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(user.getId());
            cell0.setCellStyle(dataStyle);
            
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(user.getUsername());
            cell1.setCellStyle(dataStyle);
            
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(user.getEmail());
            cell2.setCellStyle(dataStyle);
            
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(sessions.size());
            cell3.setCellStyle(dataStyle);
            
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(String.format("%.2f", totalDistance));
            cell4.setCellStyle(dataStyle);
            
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(totalStops);
            cell5.setCellStyle(dataStyle);
            
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(user.getCreatedAt().format(DATE_FORMATTER));
            cell6.setCellStyle(dateStyle);
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
        }
    }

    private void createUserDetailSheet(Sheet sheet, User user, CellStyle headerStyle, CellStyle dataStyle, CellStyle dateStyle) {
        int rowNum = 0;
        
        // User Info Section
        Row userInfoHeader = sheet.createRow(rowNum++);
        Cell userInfoCell = userInfoHeader.createCell(0);
        userInfoCell.setCellValue("USER INFORMATION");
        userInfoCell.setCellStyle(headerStyle);
        
        Row userRow1 = sheet.createRow(rowNum++);
        userRow1.createCell(0).setCellValue("Username:");
        Cell usernameCell = userRow1.createCell(1);
        usernameCell.setCellValue(user.getUsername());
        usernameCell.setCellStyle(dataStyle);
        
        Row userRow2 = sheet.createRow(rowNum++);
        userRow2.createCell(0).setCellValue("Email:");
        Cell emailCell = userRow2.createCell(1);
        emailCell.setCellValue(user.getEmail());
        emailCell.setCellStyle(dataStyle);
        
        Row userRow3 = sheet.createRow(rowNum++);
        userRow3.createCell(0).setCellValue("Created At:");
        Cell createdCell = userRow3.createCell(1);
        createdCell.setCellValue(user.getCreatedAt().format(DATE_FORMATTER));
        createdCell.setCellStyle(dateStyle);
        
        rowNum++; // Empty row
        
        // Sessions Section
        List<TrackingSession> sessions = sessionRepository.findByUserOrderByStartTimeDesc(user);
        
        Row sessionsHeader = sheet.createRow(rowNum++);
        Cell sessionsCell = sessionsHeader.createCell(0);
        sessionsCell.setCellValue("TRACKING SESSIONS");
        sessionsCell.setCellStyle(headerStyle);
        
        Row sessionHeaderRow = sheet.createRow(rowNum++);
        String[] sessionHeaders = {"Session ID", "Start Time", "End Time", "Distance (km)", "Area", "State", "Route Points", "Checkpoints"};
        for (int i = 0; i < sessionHeaders.length; i++) {
            Cell cell = sessionHeaderRow.createCell(i);
            cell.setCellValue(sessionHeaders[i]);
            cell.setCellStyle(headerStyle);
        }
        
        for (TrackingSession session : sessions) {
            Row row = sheet.createRow(rowNum++);
            
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(session.getSessionId());
            cell0.setCellStyle(dataStyle);
            
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(session.getStartTime().format(DATE_FORMATTER));
            cell1.setCellStyle(dateStyle);
            
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(session.getEndTime() != null ? session.getEndTime().format(DATE_FORMATTER) : "Ongoing");
            cell2.setCellStyle(dateStyle);
            
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(String.format("%.2f", session.getDistanceKm()));
            cell3.setCellStyle(dataStyle);
            
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(session.getAreaName() != null ? session.getAreaName() : "N/A");
            cell4.setCellStyle(dataStyle);
            
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(session.getState() != null ? session.getState() : "N/A");
            cell5.setCellStyle(dataStyle);
            
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(session.getRoutePointsCount());
            cell6.setCellStyle(dataStyle);
            
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(session.getCheckpointsCount());
            cell7.setCellStyle(dataStyle);
        }
        
        // Auto-size columns
        for (int i = 0; i < sessionHeaders.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(false);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        Font font = workbook.createFont();
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        return style;
    }

    private String sanitizeSheetName(String name) {
        // Excel sheet names can't contain: \ / ? * [ ]
        // Max length is 31 characters
        String sanitized = name.replaceAll("[\\\\/:*?\\[\\]]", "_");
        if (sanitized.length() > 31) {
            sanitized = sanitized.substring(0, 31);
        }
        return sanitized;
    }
}
