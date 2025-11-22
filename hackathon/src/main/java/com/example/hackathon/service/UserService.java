package com.example.hackathon.service;

import com.example.hackathon.models.PasswordResetToken;
import com.example.hackathon.models.User;
import com.example.hackathon.repository.PasswordResetTokenRepository;
import com.example.hackathon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Autowired
    private EmailService emailService;

    public User registerUser(User user) {
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public User login(String username, String password) {
        User user = userRepo.findByUsername(username).orElse(null);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public void generateOtp(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        
        // Delete any existing tokens for this user
        tokenRepo.deleteByUser(user);
        
        // Generate 6-digit OTP
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        
        // Save Token
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(otp);
        token.setUser(user);
        token.setExpiryDate(java.time.LocalDateTime.now().plusMinutes(15));
        tokenRepo.save(token);
        
        // Send Email
        emailService.sendOtpEmail(email, otp);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Token"));
        
        if (resetToken.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Token Expired");
        }
        
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        
        // Invalidate token (optional: delete it)
        tokenRepo.delete(resetToken);
    }

    public User getUserByUsername(String username) {
        return userRepo.findByUsername(username).orElse(null);
    }

    public User updateProfile(String username, User updatedUser) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update allowed fields
        if (updatedUser.getFirstName() != null) {
            user.setFirstName(updatedUser.getFirstName());
        }
        if (updatedUser.getLastName() != null) {
            user.setLastName(updatedUser.getLastName());
        }
        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }
        
        return userRepo.save(user);
    }

    public void changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }
}

