package com.lms.dto.requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AssignmentSubmissionRequest {
    private Long courseId;
    private String content;
    private MultipartFile file; // for the uploaded file

}
