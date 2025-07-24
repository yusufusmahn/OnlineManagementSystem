package com.lms.utils;

import com.lms.data.models.Assignment;
import com.lms.dto.responses.AssignmentResponse;

public class AssignmentMapper {

    public static AssignmentResponse toDTO(Assignment assignment) {
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .dueDate(assignment.getDueDate())
                .fileUrl(assignment.getFileUrl())
                .courseTitle(assignment.getCourse().getTitle())
                .createdAt(assignment.getCreatedAt())
                .build();
    }
}
