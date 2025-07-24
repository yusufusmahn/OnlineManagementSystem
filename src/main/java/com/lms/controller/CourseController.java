package com.lms.controller;


import com.lms.data.models.User;
import com.lms.dto.requests.CourseRequestDTO;
import com.lms.dto.requests.CourseUpdateRequest;
import com.lms.dto.responses.CourseResponseDTO;
import com.lms.dto.responses.UserResponse;
import com.lms.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(@RequestBody CourseRequestDTO dto) {
        return ResponseEntity.ok(courseService.createCourse(dto));
    }

    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<?> updateCourse(@PathVariable String courseId,
                                          @RequestBody CourseUpdateRequest request) {
        courseService.updateCourse(courseId, request);
        return ResponseEntity.ok("Course updated successfully");
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable String courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok("Course deleted successfully");
    }

    @GetMapping("/courses/{courseId}/students")
    public ResponseEntity<List<UserResponse>> getEnrolledStudents(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getEnrolledStudents(courseId));
    }


}
