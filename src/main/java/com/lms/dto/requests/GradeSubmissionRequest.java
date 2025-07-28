package com.lms.dto.requests;

import lombok.Data;

@Data
public class GradeSubmissionRequest {
    private String grade;
    private String feedback;
}
