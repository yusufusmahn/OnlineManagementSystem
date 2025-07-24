package com.lms.services;

import com.lms.data.models.AssignmentSubmission;
import com.lms.data.models.Course;
import com.lms.data.models.Role;
import com.lms.data.models.User;
import com.lms.data.respositories.AssignmentSubmissionRepository;
import com.lms.data.respositories.CourseRepository;
import com.lms.data.respositories.EnrollmentRepository;
import com.lms.dto.requests.AssignmentSubmissionRequest;
import com.lms.dto.responses.AssignmentSubmissionResponse;
import com.lms.dto.responses.StudentGradeDTO;
import com.lms.exception.*;
import com.lms.security.CurrentUserProvider;
import com.lms.services.AssignmentSubmissionService;
import com.lms.utils.AssignmentSubmissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentSubmissionServiceImpl implements AssignmentSubmissionService {


    private final AssignmentSubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public AssignmentSubmissionResponse submitAssignment(AssignmentSubmissionRequest request) {
        User student = currentUserProvider.getCurrentUser();

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        boolean enrolled = enrollmentRepository.existsByStudentAndCourse(student, course);
        if (!enrolled) {
            throw new BadRequestException("You must be enrolled in this course to submit an assignment");
        }

        AssignmentSubmission submission = AssignmentSubmission.builder()
                .course(course)
                .student(student)
                .content(request.getContent())
                .build();

        AssignmentSubmission saved = submissionRepository.save(submission);
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

    @Override
    public AssignmentSubmissionResponse gradeSubmission(Long submissionId, String grade) {
        User currentUser = currentUserProvider.getCurrentUser();

        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        Course course = submission.getCourse();

        boolean isInstructor = course.getInstructor() != null &&
                course.getInstructor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isInstructor && !isAdmin) {
            throw new UnauthorizedActionException("Only the instructor or an admin can grade this submission.");
        }

        submission.setGrade(grade);
        AssignmentSubmission updated = submissionRepository.save(submission);
        return AssignmentSubmissionMapper.toDTO(updated);
    }

    public AssignmentSubmissionResponse updateSubmission(Long submissionId, AssignmentSubmissionRequest request) {
        User currentUser = currentUserProvider.getCurrentUser(); //  authenticated student

        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission not found"));

        //  Only the original student can update
        if (!submission.getStudent().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not allowed to update this submission");
        }

        //  Prevent updates if already graded
        if (submission.getGrade() != null) {
            throw new BadRequestException("You cannot update a graded submission");
        }

        submission.setContent(request.getContent());
        AssignmentSubmission updated = submissionRepository.save(submission);
        return AssignmentSubmissionMapper.toDTO(updated);
    }



}
