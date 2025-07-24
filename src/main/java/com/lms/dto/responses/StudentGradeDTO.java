package com.lms.dto.responses;

import lombok.Data;

@Data
public class StudentGradeDTO {
    private String courseName;
    private String assignmentTitle;
    private Double grade;
    private String status;
}
