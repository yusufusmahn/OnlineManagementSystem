package com.lms.dto.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String instructorName;
    private Double price;
    private String createdAt;
}
