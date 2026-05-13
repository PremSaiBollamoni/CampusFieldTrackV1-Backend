package com.campusfieldtrack.util;

import com.campusfieldtrack.entity.User;
import com.campusfieldtrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Diagnostic utility to see what's actually in the database
 */
@Component  // ENABLED - Check if import worked
public class DiagnoseUsers implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n========================================");
        System.out.println("🔍 DATABASE DIAGNOSIS");
        System.out.println("========================================\n");
        
        List<User> allUsers = userRepository.findAll();
        
        for (User user : allUsers) {
            System.out.println("📋 User Record:");
            System.out.println("   ID: " + user.getId());
            System.out.println("   Username: '" + user.getUsername() + "'");
            System.out.println("   Emp ID: '" + user.getEmpId() + "'");
            System.out.println("   Email: " + user.getEmail());
            System.out.println("   Role: " + user.getRole());
            System.out.println("   Password (first 20 chars): " + (user.getPassword() != null ? user.getPassword().substring(0, Math.min(20, user.getPassword().length())) : "null"));
            System.out.println("   Match: " + (user.getUsername() != null && user.getUsername().equals(user.getEmpId()) ? "✅ CORRECT" : "❌ MISMATCH"));
            System.out.println();
        }
        
        System.out.println("========================================");
        System.out.println("Total users: " + allUsers.size());
        System.out.println("========================================\n");
    }
}
