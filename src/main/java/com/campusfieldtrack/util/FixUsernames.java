package com.campusfieldtrack.util;

import com.campusfieldtrack.entity.User;
import com.campusfieldtrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * One-time utility to fix usernames - change from employee names to Emp IDs
 * DISABLE THIS AFTER RUNNING ONCE by commenting @Component
 */
// @Component  // DISABLED - Running diagnosis first
public class FixUsernames implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔧 Starting username fix...");
        
        List<User> allUsers = userRepository.findAll();
        int fixed = 0;
        
        for (User user : allUsers) {
            // Skip admin
            if ("ADMIN".equals(user.getRole())) {
                System.out.println("⏭️  Skipping admin user");
                continue;
            }
            
            // Check if username is already the empId
            if (user.getUsername() != null && user.getUsername().equals(user.getEmpId())) {
                System.out.println("✓ User " + user.getUsername() + " already correct");
                continue;
            }
            
            // Check if empId exists
            if (user.getEmpId() == null || user.getEmpId().isBlank()) {
                System.out.println("⚠️  User " + user.getUsername() + " has no empId, skipping");
                continue;
            }
            
            String oldUsername = user.getUsername();
            String newUsername = user.getEmpId();
            
            // Update username to empId
            user.setUsername(newUsername);
            userRepository.save(user);
            
            System.out.println("✅ Fixed: '" + oldUsername + "' → '" + newUsername + "'");
            fixed++;
        }
        
        System.out.println("🎉 Username fix complete! Fixed " + fixed + " users");
        System.out.println("⚠️  IMPORTANT: Disable this utility by commenting @Component annotation");
    }
}
