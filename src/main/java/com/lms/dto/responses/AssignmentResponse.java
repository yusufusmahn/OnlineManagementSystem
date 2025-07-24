package com.lms.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AssignmentResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String fileUrl;
    private String courseTitle;
    private LocalDateTime createdAt;
}
