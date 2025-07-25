package com.lms.data.respositories;

import com.lms.data.models.AssignmentSubmission;
import com.lms.data.models.Course;
import com.lms.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    List<AssignmentSubmission> findByCourse(Course course);
    List<AssignmentSubmission> findByStudent(User student);
    List<AssignmentSubmission> findByStudentAndGradeIsNotNull(User student);

}
