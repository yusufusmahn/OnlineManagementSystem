package com.lms.services;

import com.lms.data.models.*;
import com.lms.data.respositories.*;
import com.lms.dto.requests.*;
import com.lms.dto.responses.*;
import com.lms.exception.*;
import com.lms.security.CurrentUserProvider;
import com.lms.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentSubmissionServiceImpl implements AssignmentSubmissionService {


    private final AssignmentSubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CurrentUserProvider currentUserProvider;
    private final EmailService emailService; //  Inject this
    private final CloudinaryService cloudinaryService;




    public AssignmentSubmissionResponse submitAssignment(AssignmentSubmissionRequest request) {
        User student = currentUserProvider.getCurrentUser();

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        boolean enrolled = enrollmentRepository.existsByStudentAndCourse(student, course);
        if (!enrolled) {
            throw new BadRequestException("You must be enrolled in this course to submit an assignment");
        }

        String uploadedFileUrl = null;
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            try {
                uploadedFileUrl = cloudinaryService.uploadFile(request.getFile());
            } catch (Exception e) {
                throw new FileUploadException("Failed to upload assignment file: " + e.getMessage());
            }
        }

        AssignmentSubmission submission = AssignmentSubmission.builder()
                .course(course)
                .student(student)
                .content(request.getContent())
                .fileUrl(uploadedFileUrl)
                .build();

        AssignmentSubmission saved = submissionRepository.save(submission);

        // Send email
        try {
            emailService.sendEmail(
                    student.getEmail(),
                    "Assignment Submitted",
                    "Hi " + student.getName() + ",\n\nYour assignment for \"" + course.getTitle() +
                            "\" was successfully submitted."
            );
        } catch (Exception e) {
            System.err.println("Failed to send submission confirmation email: " + e.getMessage());
        }

        return AssignmentSubmissionMapper.toDTO(saved);
    }


    @Override
    public List<AssignmentSubmissionResponse> getMySubmissions() {
        User currentUser = currentUserProvider.getCurrentUser();

        List<AssignmentSubmission> submissions = submissionRepository.findByStudent(currentUser);

        return submissions.stream()
                .map(AssignmentSubmissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentSubmissionResponse> getSubmissionsByCourse(Long courseId) {
        User currentUser = currentUserProvider.getCurrentUser();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        boolean isInstructor = course.getInstructor() != null &&
                course.getInstructor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isInstructor && !isAdmin) {
            throw new UnauthorizedActionException("You are not authorized to view submissions for this course");
        }

        List<AssignmentSubmission> submissions = submissionRepository.findByCourse(course);

        return submissions.stream()
                .map(AssignmentSubmissionMapper::toDTO)
                .collect(Collectors.toList());
    }

//    @Override
//    public AssignmentSubmissionResponse gradeSubmission(Long submissionId, String grade) {
//        User currentUser = currentUserProvider.getCurrentUser();
//
//        AssignmentSubmission submission = submissionRepository.findById(submissionId)
//                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
//
//        Course course = submission.getCourse();
//        User student = submission.getStudent(); // get student from submission
//
//        try {
//            emailService.sendEmail(
//                    student.getEmail(),
//                    "Assignment Graded",
//                    "Hi " + student.getName() + ",\n\nYour assignment in \"" + course.getTitle() + "\" has been graded. Grade: " + grade
//            );
//        } catch (Exception e) {
//            System.err.println("Failed to send grading notification email: " + e.getMessage());
//        }
//
//
//        boolean isInstructor = course.getInstructor() != null &&
//                course.getInstructor().getId().equals(currentUser.getId());
//        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
//
//        if (!isInstructor && !isAdmin) {
//            throw new UnauthorizedActionException("Only the instructor or an admin can grade this submission.");
//        }
//
//        submission.setGrade(grade);
//        AssignmentSubmission updated = submissionRepository.save(submission);
//        return AssignmentSubmissionMapper.toDTO(updated);
//    }

    @Override
    public AssignmentSubmissionResponse gradeSubmission(Long submissionId, GradeSubmissionRequest request) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        // Prevent re-grading if already graded
        if (submission.getGrade() != null) {
            throw new BadRequestException("This submission has already been graded and cannot be re-graded.");
        }


        User instructorOrAdmin = currentUserProvider.getCurrentUser();
        User student = submission.getStudent();
        Course course = submission.getCourse();

        if (!(instructorOrAdmin.getRole() == Role.ADMIN ||
                (instructorOrAdmin.getRole() == Role.INSTRUCTOR &&
                        course.getInstructor().getId().equals(instructorOrAdmin.getId())))) {
            throw new UnauthorizedActionException("You are not authorized to grade this submission");
        }

        submission.setGrade(request.getGrade());
        submission.setFeedback(request.getFeedback()); //  Set feedback
        submission.setGradedAt(LocalDateTime.now());

        AssignmentSubmission saved = submissionRepository.save(submission);

        try {
            emailService.sendEmail(
                    student.getEmail(),
                    "Your Assignment Has Been Graded",
                    "Hello " + student.getName() + ",\n\n" +
                            "Your submission for \"" + course.getTitle() + "\" has been graded.\n\n" +
                            "Grade: " + request.getGrade() + "\n" +
                            "Feedback: " + request.getFeedback() + "\n\n" +
                            "You can log in to view more details.\n\n" +
                            "Best regards,\nYour LMS Team"
            );
        } catch (Exception e) {
            System.err.println("Failed to send grading email: " + e.getMessage());
        }

        return AssignmentSubmissionMapper.toDTO(saved);
    }





    public AssignmentSubmissionResponse updateSubmission(Long submissionId, AssignmentSubmissionRequest request) {
        User student = currentUserProvider.getCurrentUser();

        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        // Ensure the submission belongs to the current student
        if (!submission.getStudent().getId().equals(student.getId())) {
            throw new UnauthorizedException("You can only update your own submissions.");
        }

        // Update content if provided
        if (request.getContent() != null && !request.getContent().isBlank()) {
            submission.setContent(request.getContent());
        }

        // Upload and update file if provided
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            try {
                String newFileUrl = cloudinaryService.uploadFile(request.getFile());
                submission.setFileUrl(newFileUrl);
            } catch (Exception e) {
                throw new FileUploadException("Failed to upload file: " + e.getMessage());
            }
        }

        AssignmentSubmission updatedSubmission = submissionRepository.save(submission);

        return AssignmentSubmissionMapper.toDTO(updatedSubmission);
    }

    @Override
    public List<AssignmentSubmissionResponse> getGradedSubmissionsForCurrentStudent() {
        User student = currentUserProvider.getCurrentUser();

        List<AssignmentSubmission> gradedSubmissions = submissionRepository.findByStudentAndGradeIsNotNull(student);

        return gradedSubmissions.stream()
                .map(AssignmentSubmissionMapper::toDTO)
                .collect(Collectors.toList());
    }



}
