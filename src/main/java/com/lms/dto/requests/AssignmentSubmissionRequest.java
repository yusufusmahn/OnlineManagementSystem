package com.lms.dto.requests;

import lombok.Data;

@Data
public class AssignmentSubmissionRequest {
    private Long courseId;
    private String content;
}
