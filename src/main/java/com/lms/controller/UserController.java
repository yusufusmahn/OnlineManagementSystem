package com.lms.controller;

import com.lms.dto.requests.CreateInstructorRequestDTO;
import com.lms.dto.requests.UserRequestDTO;
import com.lms.dto.responses.UserResponse;
import com.lms.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @RequestBody UserRequestDTO dto) {
        UserResponse response = userService.updateUser(id, dto);
        return ResponseEntity.ok(response);
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/instructors")
    public ResponseEntity<UserResponse> createInstructor(@RequestBody CreateInstructorRequestDTO dto) {
        UserResponse instructor = userService.createInstructor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(instructor);
    }

    @DeleteMapping("/instructors/{instructorId}")
    public ResponseEntity<?> deleteInstructor(@PathVariable UUID instructorId) {
        userService.deleteInstructor(instructorId);
        return ResponseEntity.ok("Instructor deleted successfully.");
    }


    @GetMapping("/instructors")
    public ResponseEntity<List<UserResponse>> getAllInstructors() {
        return ResponseEntity.ok(userService.getAllInstructors());
    }

}
