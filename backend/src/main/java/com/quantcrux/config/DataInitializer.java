package com.quantcrux.config;

import com.quantcrux.model.User;
import com.quantcrux.repository.UserRepository;
import com.quantcrux.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SessionService sessionService;

    @Override
    public void run(String... args) throws Exception {
        // Clean up expired sessions on startup
        sessionService.cleanupExpiredSessions();
        
        // Create demo users if they don't exist
        createUserIfNotExists("admin", "admin@quantcrux.com", "System Administrator", User.Role.ADMIN);
        createUserIfNotExists("client1", "client1@quantcrux.com", "John Client", User.Role.CLIENT);
        createUserIfNotExists("client2", "client2@quantcrux.com", "Jane Investor", User.Role.CLIENT);
        createUserIfNotExists("pm1", "pm1@quantcrux.com", "Michael Portfolio", User.Role.PORTFOLIO_MANAGER);
        createUserIfNotExists("pm2", "pm2@quantcrux.com", "Sarah Manager", User.Role.PORTFOLIO_MANAGER);
        createUserIfNotExists("researcher1", "researcher1@quantcrux.com", "David Research", User.Role.RESEARCHER);
        createUserIfNotExists("researcher2", "researcher2@quantcrux.com", "Lisa Analyst", User.Role.RESEARCHER);
        
        System.out.println("=== QuantCrux Demo Users ===");
        System.out.println("All demo users have password: 'password'");
        System.out.println("Available roles: CLIENT, PORTFOLIO_MANAGER, RESEARCHER, ADMIN");
        System.out.println("============================");
    }

    private void createUserIfNotExists(String username, String email, String name, User.Role role) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setName(name);
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole(role);
            user.setIsActive(true);
            user.setIsEmailVerified(true);
            user.setCreatedBy("system");
            
            // Set additional demo data
            switch (role) {
                case CLIENT:
                    user.setDepartment("Investment");
                    user.setPhoneNumber("+1-555-010" + (username.charAt(username.length()-1)));
                    break;
                case PORTFOLIO_MANAGER:
                    user.setDepartment("Portfolio Management");
                    user.setPhoneNumber("+1-555-020" + (username.charAt(username.length()-1)));
                    break;
                case RESEARCHER:
                    user.setDepartment("Quantitative Research");
                    user.setPhoneNumber("+1-555-030" + (username.charAt(username.length()-1)));
                    break;
                case ADMIN:
                    user.setDepartment("IT");
                    user.setPhoneNumber("+1-555-0001");
                    break;
            }
            
            userRepository.save(user);
            System.out.println("Created demo user: " + username + " (" + role + ")");
        }
    }
}