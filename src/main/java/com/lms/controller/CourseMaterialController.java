package com.lms.controller;

import com.lms.dto.requests.CourseMaterialUploadRequest;
import com.lms.dto.responses.CourseMaterialResponse;
import com.lms.services.CourseMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class CourseMaterialController {

    private final CourseMaterialService courseMaterialService;

    @PostMapping("/upload")
    public ResponseEntity<CourseMaterialResponse> upload(@ModelAttribute CourseMaterialUploadRequest request) {
        CourseMaterialResponse response = courseMaterialService.uploadMaterial(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CourseMaterialResponse>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseMaterialService.getMaterialsByCourse(courseId));
    }
}
