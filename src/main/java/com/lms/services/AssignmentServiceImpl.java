package com.lms.services;

import com.lms.data.models.Assignment;
import com.lms.data.models.*;
import com.lms.data.respositories.*;
import com.lms.dto.requests.AssignmentRequest;
import com.lms.dto.responses.*;
import com.lms.exception.*;
import com.lms.security.CurrentUserProvider;
import com.lms.utils.AssignmentMapper;
import com.lms.utils.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final CloudinaryService cloudinaryService;
    private final EmailService emailService;
    private final EnrollmentRepository enrollmentRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public AssignmentResponse createAssignment(AssignmentRequest request, MultipartFile file) {
        User currentUser = currentUserProvider.getCurrentUser();

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        //  Ensure only the instructor or admin can create assignment
        boolean isInstructor = course.getInstructor() != null &&
                course.getInstructor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isInstructor && !isAdmin) {
            throw new UnauthorizedActionException("Only the course instructor or an admin can create assignments.");
        }

        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            fileUrl = cloudinaryService.uploadFile(file);
        }

        Assignment assignment = Assignment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .fileUrl(fileUrl)
                .course(course)
                .build();

        assignment = assignmentRepository.save(assignment);

        // Notify enrolled students via email
        List<Enrollment> enrollments = enrollmentRepository.findByCourse(course);
        for (Enrollment e : enrollments) {
            try {
                emailService.sendEmail(
                        e.getStudent().getEmail(),
                        "New Assignment Posted",
                        "Hi " + e.getStudent().getName() + ",\n\nA new assignment has been posted in \"" + course.getTitle() + "\":\n\n" +
                                assignment.getTitle() + "\nDue: " + assignment.getDueDate()
                );
            } catch (Exception ex) {
                System.err.println("Failed to notify student: " + e.getStudent().getEmail());
            }
        }

        return AssignmentMapper.toDTO(assignment);
    }



    @Override
    public List<AssignmentResponse> getAssignmentsByCourse(Long courseId) {
        User currentUser = currentUserProvider.getCurrentUser();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        boolean isInstructor = course.getInstructor() != null &&
                course.getInstructor().getId().equals(currentUser.getId());

        boolean isStudent = currentUser.getRole() == Role.STUDENT &&
                enrollmentRepository.existsByStudentAndCourse(currentUser, course);

        if (!isAdmin && !isInstructor && !isStudent) {
            throw new UnauthorizedActionException("You are not allowed to view assignments for this course.");
        }

        return assignmentRepository.findByCourseId(courseId)
                .stream()
                .map(AssignmentMapper::toDTO)
                .toList();
    }



}
