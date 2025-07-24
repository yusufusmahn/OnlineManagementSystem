package com.lms.dto.responses;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AssignmentSubmissionResponse {
    private Long id;
    private UUID studentId;
    private Long courseId;
    private String content;
    private String grade; //  New field
    private LocalDateTime submittedAt;
}
