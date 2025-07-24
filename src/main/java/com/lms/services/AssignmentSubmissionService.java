package com.lms.services;

import com.lms.dto.requests.AssignmentSubmissionRequest;
import com.lms.dto.responses.AssignmentSubmissionResponse;

import java.util.List;

public interface AssignmentSubmissionService {
    AssignmentSubmissionResponse submitAssignment(AssignmentSubmissionRequest request);
    List<AssignmentSubmissionResponse> getMySubmissions();
    List<AssignmentSubmissionResponse> getSubmissionsByCourse(Long courseId);
    AssignmentSubmissionResponse gradeSubmission(Long submissionId, String grade);
    AssignmentSubmissionResponse updateSubmission(Long submissionId, AssignmentSubmissionRequest request);


}
