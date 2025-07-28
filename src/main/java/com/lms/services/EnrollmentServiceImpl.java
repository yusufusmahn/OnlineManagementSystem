package com.lms.services;

import com.lms.data.models.Course;
import com.lms.data.models.Enrollment;
import com.lms.data.models.User;
import com.lms.data.respositories.CourseRepository;
import com.lms.data.respositories.EnrollmentRepository;
import com.lms.exception.BadRequestException;
import com.lms.exception.ResourceNotFoundException;
import com.lms.security.CurrentUserProvider;
import com.lms.utils.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final CurrentUserProvider currentUserProvider;
    private final EmailService emailService;

    @Override
    public void enrollInCourse(String courseId) {
        User student = currentUserProvider.getCurrentUser();

        if (!student.getRole().name().equals("STUDENT")) {
            throw new BadRequestException("Only students can enroll in courses");
        }

        Course course = courseRepository.findById(Long.parseLong(courseId))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new BadRequestException("You are already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();

        enrollmentRepository.save(enrollment);

        try {
            emailService.sendEmail(
                    student.getEmail(),
                    "Course Enrollment Successful",
                    "Hi " + student.getName() + ",\n\nYou've successfully enrolled in the course: " + course.getTitle()
            );
        } catch (Exception e) {
            System.err.println("Failed to send enrollment email: " + e.getMessage());
        }

    }

    @Override
    public List<Course> getEnrolledCourses() {
        User student = currentUserProvider.getCurrentUser();

        if (!student.getRole().name().equals("STUDENT")) {
            throw new BadRequestException("Only students can view enrolled courses");
        }

        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);
        return enrollments.stream()
                .map(Enrollment::getCourse)
                .toList();
    }

}
