package com.digviajay.taskflow.service;

import com.digviajay.taskflow.entity.User;
import com.digviajay.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");

    public User register(String username, String email, String password) {
        if (username == null || username.trim().length() < 3)
            throw new RuntimeException("Username must be at least 3 characters");

        if (!EMAIL_PATTERN.matcher(email.trim()).matches())
            throw new RuntimeException("Invalid email format");

        if (!PASSWORD_PATTERN.matcher(password).matches())
            throw new RuntimeException("Password must be at least 8 characters with uppercase, lowercase and a number");

        if (userRepository.existsByEmail(email.trim().toLowerCase()))
            throw new RuntimeException("Email already in use");

        if (userRepository.existsByUsername(username.trim()))
            throw new RuntimeException("Username already taken");

        User user = new User();
        user.setUsername(username.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        if (email == null || email.trim().isEmpty())
            throw new RuntimeException("Email is required");

        if (password == null || password.trim().isEmpty())
            throw new RuntimeException("Password is required");

        // lowercase on login to match how we stored it
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new RuntimeException("Incorrect password");

        return user;
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> searchUsers(String keyword) {
        return userRepository
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void changePassword(User user, String currentPassword, String newPassword) {
        if (!passwordEncoder.matches(currentPassword, user.getPassword()))
            throw new RuntimeException("Current password is incorrect");

        if (!PASSWORD_PATTERN.matcher(newPassword).matches())
            throw new RuntimeException("New password must be at least 8 characters with uppercase, lowercase and a number");

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}