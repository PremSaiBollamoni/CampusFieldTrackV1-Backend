package com.campusfieldtrack.util;

import com.campusfieldtrack.entity.User;
import com.campusfieldtrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * One-time utility to delete all employee users (keeps admin)
 * DISABLE THIS AFTER RUNNING ONCE by commenting @Component
 */
// @Component  // DISABLED - Already ran successfully
public class DeleteAllEmployees implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n========================================");
        System.out.println("🗑️  DELETING ALL EMPLOYEE USERS");
        System.out.println("========================================\n");
        
        List<User> allUsers = userRepository.findAll();
        int deleted = 0;
        
        for (User user : allUsers) {
            // Skip admin
            if ("ADMIN".equals(user.getRole())) {
                System.out.println("⏭️  Keeping admin user: " + user.getUsername());
                continue;
            }
            
            System.out.println("🗑️  Deleting: " + user.getUsername() + " (ID: " + user.getId() + ")");
            userRepository.delete(user);
            deleted++;
        }
        
        System.out.println("\n========================================");
        System.out.println("✅ Deleted " + deleted + " employee users");
        System.out.println("✅ Admin user preserved");
        System.out.println("⚠️  IMPORTANT: Disable this utility NOW!");
        System.out.println("========================================\n");
    }
}
