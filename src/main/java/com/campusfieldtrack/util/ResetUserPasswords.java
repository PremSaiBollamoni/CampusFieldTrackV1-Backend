package com.campusfieldtrack.util;

import com.campusfieldtrack.entity.User;
import com.campusfieldtrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

// @Component - Disabled after fixing passwords
public class ResetUserPasswords implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n========================================");
        System.out.println("FIXING USER DATA AND PASSWORDS");
        System.out.println("========================================");

        List<User> users = userRepository.findAll();
        int fixedCount = 0;

        for (User user : users) {
            if (!"ADMIN".equals(user.getRole())) {
                // Fix empId to match username (username is correct, empId has names)
                if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                    user.setEmpId(user.getUsername());
                    
                    // Reset password to {Username}@123
                    String newPassword = user.getUsername() + "@123";
                    user.setPassword(passwordEncoder.encode(newPassword));
                    
                    userRepository.save(user);
                    fixedCount++;
                    System.out.println("✅ Fixed: " + user.getUsername() + " - Password: " + newPassword);
                }
            }
        }

        System.out.println("\n✅ Fixed " + fixedCount + " users");
        System.out.println("Password format: {Username}@123");
        System.out.println("Example: TS0271@123");
        System.out.println("========================================\n");
    }
}
