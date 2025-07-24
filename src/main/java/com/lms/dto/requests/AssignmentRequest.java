package com.lms.dto.requests;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentRequest {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Long courseId;
}
