package com.lms.data.respositories;

import com.lms.data.models.Course;
import com.lms.data.models.Enrollment;
import com.lms.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudentAndCourse(User student, Course course);
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    List<Enrollment> findByStudent(User student);
    List<Enrollment> findByCourse(Course course);


}
