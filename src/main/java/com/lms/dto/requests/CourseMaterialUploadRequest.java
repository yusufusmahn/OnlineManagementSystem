package com.lms.dto.requests;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseMaterialUploadRequest {

    private String title;
    private String type;
    private Long courseId;
    private MultipartFile file;
}
