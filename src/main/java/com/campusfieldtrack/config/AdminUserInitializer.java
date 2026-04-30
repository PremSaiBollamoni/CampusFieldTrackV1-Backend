package com.campusfieldtrack.config;

import com.campusfieldtrack.entity.User;
import com.campusfieldtrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Check if admin user already exists
        if (!userRepository.existsByUsername("ADMIN-CFT")) {
            User admin = User.builder()
                .username("ADMIN-CFT")
                .email("cftadmin@cft.in")
                .password(passwordEncoder.encode("AdminCft@$"))
                .role("ADMIN")
                .build();
            
            userRepository.save(admin);
            System.out.println("✅ Admin user created: ADMIN-CFT");
        } else {
            System.out.println("ℹ️ Admin user already exists");
        }
    }
}
