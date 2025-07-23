package com.lms.services;

import com.lms.data.models.User;
import com.lms.data.respositories.UserRepository;
import com.lms.dto.requests.LoginRequest;
import com.lms.dto.responses.LoginResponse;
import com.lms.exception.InvalidCredentialsException;
import com.lms.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public LoginResponse login(LoginRequest request) {
        // Normalize email
        String email = request.getEmail().toLowerCase();

        // Authenticate using Spring Security
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        // Fetch user
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Generate JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new LoginResponse(user.getName(), user.getEmail(), user.getRole(), token);
    }
}
