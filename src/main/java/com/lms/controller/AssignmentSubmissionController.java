package com.lms.controller;

import com.lms.dto.requests.AssignmentSubmissionRequest;
import com.lms.dto.responses.AssignmentSubmissionResponse;
import com.lms.services.AssignmentSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentSubmissionController {

    private final AssignmentSubmissionService assignmentSubmissionService;

    @PostMapping("/submit")
    public ResponseEntity<AssignmentSubmissionResponse> submitAssignment(
            @Valid @RequestBody AssignmentSubmissionRequest request) {
        AssignmentSubmissionResponse response = assignmentSubmissionService.submitAssignment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-submissions")
    public ResponseEntity<List<AssignmentSubmissionResponse>> getMySubmissions() {
        return ResponseEntity.ok(assignmentSubmissionService.getMySubmissions());
    }

    @GetMapping("/course/{courseId}/submissions")
    public ResponseEntity<List<AssignmentSubmissionResponse>> getSubmissionsByCourse(
            @PathVariable Long courseId
    ) {
        List<AssignmentSubmissionResponse> responses = assignmentSubmissionService.getSubmissionsByCourse(courseId);
        return ResponseEntity.ok(responses);
    }


    @PostMapping("/{submissionId}/grade")
    public ResponseEntity<AssignmentSubmissionResponse> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestParam String grade
    ) {
        AssignmentSubmissionResponse response = assignmentSubmissionService.gradeSubmission(submissionId, grade);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{submissionId}")
    public ResponseEntity<AssignmentSubmissionResponse> updateSubmission(
            @PathVariable Long submissionId,
            @RequestBody AssignmentSubmissionRequest request) {

        AssignmentSubmissionResponse updated = assignmentSubmissionService.updateSubmission(submissionId, request);
        return ResponseEntity.ok(updated);
    }


}
