package com.lms.utils;

import com.lms.data.models.AssignmentSubmission;
import com.lms.dto.responses.AssignmentSubmissionResponse;
import com.lms.dto.responses.StudentGradeDTO;

public class AssignmentSubmissionMapper {

    public static AssignmentSubmissionResponse toDTO(AssignmentSubmission submission) {
        AssignmentSubmissionResponse dto = new AssignmentSubmissionResponse();
        dto.setId(submission.getId());
        dto.setStudentId(submission.getStudent().getId());
        dto.setCourseId(submission.getCourse().getId());
        dto.setContent(submission.getContent());
        dto.setGrade(submission.getGrade()); // Include grade
        dto.setSubmittedAt(submission.getSubmittedAt());
        return dto;
    }

//    public static StudentGradeDTO toStudentGradeDTO(AssignmentSubmission submission) {
//        StudentGradeDTO dto = new StudentGradeDTO();
//        dto.setCourseName(submission.getAssignment().getCourse().getTitle());
//        dto.setAssignmentTitle(submission.getAssignment().getTitle());
//        dto.setGrade(submission.getGrade());
//        dto.setStatus(submission.getStatus().name());
//        return dto;
//    }


}
