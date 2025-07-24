package com.lms.controller;

import com.lms.services.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/{courseId}")
    public ResponseEntity<?> enroll(@PathVariable String courseId) {
        enrollmentService.enrollInCourse(courseId);
        return ResponseEntity.ok("Enrolled successfully");
    }

    @GetMapping
    public ResponseEntity<?> getMyEnrolledCourses() {
        return ResponseEntity.ok(enrollmentService.getEnrolledCourses());
    }

}
