package com.campusfieldtrack.util;

import com.campusfieldtrack.entity.User;
import com.campusfieldtrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * One-time utility to fix employee passwords that were manually inserted
 * Encodes plain text passwords to BCrypt format
 * DISABLE THIS AFTER RUNNING ONCE by commenting @Component
 */
// @Component  // DISABLED - Already ran successfully
public class FixEmployeePasswords implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔧 Starting password fix for employees...");
        
        List<User> allUsers = userRepository.findAll();
        int fixed = 0;
        
        for (User user : allUsers) {
            // Skip admin
            if ("ADMIN".equals(user.getRole())) {
                continue;
            }
            
            // Check if password is already BCrypt encoded (starts with $2a$ or $2b$)
            String currentPassword = user.getPassword();
            if (currentPassword != null && (currentPassword.startsWith("$2a$") || currentPassword.startsWith("$2b$"))) {
                System.out.println("✓ User " + user.getUsername() + " already has encoded password");
                continue;
            }
            
            // Password format should be: {EmpID}@123
            String expectedPassword = user.getEmpId() + "@123";
            
            // Encode and update
            user.setPassword(passwordEncoder.encode(expectedPassword));
            userRepository.save(user);
            
            System.out.println("✅ Fixed password for user: " + user.getUsername() + " (empId: " + user.getEmpId() + ")");
            fixed++;
        }
        
        System.out.println("🎉 Password fix complete! Fixed " + fixed + " users");
        System.out.println("⚠️  IMPORTANT: Disable this utility by commenting @Component annotation");
    }
}
