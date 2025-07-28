package com.lms.services;

import com.lms.data.models.User;
import com.lms.data.respositories.UserRepository;
import com.lms.dto.requests.LoginRequest;
import com.lms.dto.requests.NewPasswordRequest;
import com.lms.dto.requests.PasswordResetRequest;
import com.lms.dto.responses.LoginResponse;
import com.lms.exception.InvalidCredentialsException;
import com.lms.security.JwtUtil;
import com.lms.utils.EmailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;



    public ResponseEntity<LoginResponse> login(LoginRequest request, HttpServletResponse response) {
        String email = request.getEmail().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);

        response.addCookie(cookie);

        LoginResponse loginResponse = new LoginResponse(user.getName(), user.getEmail(), user.getRole(), token);

        return ResponseEntity.ok(loginResponse);
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtUtil.generatePasswordResetToken(user.getEmail());

        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        String emailBody = "Hi " + user.getName() + ",\n\n" +
                "Click the link below to reset your password:\n" +
                resetLink + "\n\n" +
                "This link is valid for 15 minutes.";

        try {
            emailService.sendEmail(user.getEmail(), "Password Reset Request", emailBody);
            System.out.println("📧 Password reset email sent to " + user.getEmail());
        } catch (IOException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void resetPassword(String token, String newPassword) {
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }

        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }




}
