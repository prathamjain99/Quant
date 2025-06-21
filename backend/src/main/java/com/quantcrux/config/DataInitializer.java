package com.quantcrux.config;

import com.quantcrux.model.User;
import com.quantcrux.repository.UserRepository;
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

    @Override
    public void run(String... args) throws Exception {
        // Create demo users if they don't exist
        createUserIfNotExists("client1", "client1@quantcrux.com", "Client One", User.Role.CLIENT);
        createUserIfNotExists("pm1", "pm1@quantcrux.com", "Portfolio Manager One", User.Role.PORTFOLIO_MANAGER);
        createUserIfNotExists("researcher1", "researcher1@quantcrux.com", "Researcher One", User.Role.RESEARCHER);
    }

    private void createUserIfNotExists(String username, String email, String name, User.Role role) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setName(name);
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole(role);
            userRepository.save(user);
            System.out.println("Created demo user: " + username + " with role: " + role);
        }
    }
}