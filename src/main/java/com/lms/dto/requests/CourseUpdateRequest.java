// CourseUpdateRequest.java
package com.lms.dto.requests;

import lombok.Data;

@Data
public class CourseUpdateRequest {
    private String title;
    private String description;
    private Double price;
}
