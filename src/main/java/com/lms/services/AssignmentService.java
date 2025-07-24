package com.lms.services;

import com.lms.dto.requests.AssignmentRequest;
import com.lms.dto.responses.AssignmentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AssignmentService {
    AssignmentResponse createAssignment(AssignmentRequest request, MultipartFile file);
    List<AssignmentResponse> getAssignmentsByCourse(Long courseId);
}
