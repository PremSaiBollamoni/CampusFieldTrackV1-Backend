package com.campusfieldtrack.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

// @Component - Disabled after successful migration
public class DatabaseMigration implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            System.out.println("\n========================================");
            System.out.println("DATABASE MIGRATION CHECK");
            System.out.println("========================================");
            
            // Check current database
            ResultSet rs = stmt.executeQuery("SELECT DATABASE()");
            if (rs.next()) {
                System.out.println("✅ Current database: " + rs.getString(1));
            }
            
            // Check if campustrack database exists
            rs = stmt.executeQuery("SHOW DATABASES LIKE 'campustrack'");
            boolean campusTrackExists = rs.next();
            
            if (campusTrackExists) {
                System.out.println("⚠️  Found 'campustrack' database");
                
                // Count users in campustrack
                rs = stmt.executeQuery("SELECT COUNT(*) FROM campustrack.users");
                if (rs.next()) {
                    int campusTrackUsers = rs.getInt(1);
                    System.out.println("   - campustrack.users: " + campusTrackUsers + " users");
                }
                
                // Count users in campusfieldtrack
                rs = stmt.executeQuery("SELECT COUNT(*) FROM campusfieldtrack.users");
                if (rs.next()) {
                    int campusFieldTrackUsers = rs.getInt(1);
                    System.out.println("   - campusfieldtrack.users: " + campusFieldTrackUsers + " users");
                }
                
                // Migrate data
                System.out.println("\n🔄 Migrating users from campustrack to campusfieldtrack...");
                
                // First, check what columns exist in campustrack.users
                rs = stmt.executeQuery("SHOW COLUMNS FROM campustrack.users");
                System.out.println("   Columns in campustrack.users:");
                while (rs.next()) {
                    System.out.println("     - " + rs.getString("Field"));
                }
                
                // Check columns in campusfieldtrack.users
                rs = stmt.executeQuery("SHOW COLUMNS FROM campusfieldtrack.users");
                System.out.println("   Columns in campusfieldtrack.users:");
                while (rs.next()) {
                    System.out.println("     - " + rs.getString("Field"));
                }
                
                // Migrate with correct column names and collation handling
                int migrated = stmt.executeUpdate(
                    "INSERT INTO campusfieldtrack.users " +
                    "(username, email, password, role, emp_id, employment_type, designation, project_assigned, created_at, updated_at) " +
                    "SELECT username, email, password, role, emp_id, employment_type, designation, project_assigned, created_at, updated_at " +
                    "FROM campustrack.users " +
                    "WHERE NOT EXISTS (" +
                    "  SELECT 1 FROM campusfieldtrack.users " +
                    "  WHERE campusfieldtrack.users.username COLLATE utf8mb4_unicode_ci = campustrack.users.username COLLATE utf8mb4_unicode_ci" +
                    ")"
                );
                System.out.println("✅ Migrated " + migrated + " users");
                
                // Show final count
                rs = stmt.executeQuery("SELECT COUNT(*) FROM campusfieldtrack.users");
                if (rs.next()) {
                    System.out.println("✅ Total users in campusfieldtrack: " + rs.getInt(1));
                }
                
                // List all users
                System.out.println("\n📋 Users in campusfieldtrack:");
                rs = stmt.executeQuery("SELECT username, email, role FROM campusfieldtrack.users ORDER BY created_at DESC");
                while (rs.next()) {
                    System.out.println("   - " + rs.getString("username") + 
                                     " (" + rs.getString("email") + ") - " + 
                                     rs.getString("role"));
                }
                
            } else {
                System.out.println("✅ No 'campustrack' database found - migration not needed");
                
                // Just show current users
                rs = stmt.executeQuery("SELECT COUNT(*) FROM campusfieldtrack.users");
                if (rs.next()) {
                    System.out.println("✅ Total users in campusfieldtrack: " + rs.getInt(1));
                }
            }
            
            System.out.println("========================================\n");
            
        } catch (Exception e) {
            System.err.println("❌ Migration error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
