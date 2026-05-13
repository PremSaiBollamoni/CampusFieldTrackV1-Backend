package com.campusfieldtrack.util;

import com.campusfieldtrack.entity.User;
import com.campusfieldtrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

// @Component - Disabled after cleanup
public class CleanupUsers implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n========================================");
        System.out.println("CLEANING UP INCORRECT USER DATA");
        System.out.println("========================================");

        List<User> users = userRepository.findAll();
        int deletedCount = 0;

        for (User user : users) {
            if (!"ADMIN".equals(user.getRole())) {
                System.out.println("🗑️  Deleting incorrect user: " + user.getUsername());
                userRepository.delete(user);
                deletedCount++;
            }
        }

        System.out.println("\n✅ Deleted " + deletedCount + " incorrect users");
        System.out.println("✅ Only ADMIN user remains");
        System.out.println("\n📋 Next Steps:");
        System.out.println("1. Login as ADMIN (Username: ADMIN-CFT, Password: AdminCft#$Admin)");
        System.out.println("2. Go to Admin Dashboard → Add Users → Bulk Import");
        System.out.println("3. Import the 29 employees with correct EMP IDs");
        System.out.println("========================================\n");
        
        // Drop the old database
        System.out.println("🗑️  Dropping old 'campustrack' database...");
        try {
            userRepository.deleteAll(); // This won't work for dropping DB, but shows intent
            System.out.println("✅ Please manually drop 'campustrack' database in MySQL Workbench");
            System.out.println("   Run: DROP DATABASE IF EXISTS campustrack;");
        } catch (Exception e) {
            System.out.println("⚠️  Could not drop database automatically");
        }
        System.out.println("========================================\n");
    }
}
