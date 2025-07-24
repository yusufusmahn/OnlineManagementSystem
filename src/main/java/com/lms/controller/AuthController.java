package com.lms.controller;


import com.lms.dto.requests.LoginRequest;
import com.lms.dto.requests.PasswordResetRequest;
import com.lms.dto.requests.UserRequestDTO;
import com.lms.dto.responses.LoginResponse;
import com.lms.dto.responses.UserResponse;
import com.lms.services.AuthenticationService;
import com.lms.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;



    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return authenticationService.login(request, response);
    }

    @PostMapping("/auth/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestParam String email) {
        authenticationService.requestPasswordReset(email);
        return ResponseEntity.ok("Password reset link sent to email");
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token,
                                           @RequestParam String newPassword) {
        authenticationService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password updated successfully");
    }


}
