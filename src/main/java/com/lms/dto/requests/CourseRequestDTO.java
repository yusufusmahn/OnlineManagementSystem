package com.lms.dto.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequestDTO {
    private String title;
    private String description;
//    private String instructorName;
    private Double price;
}
