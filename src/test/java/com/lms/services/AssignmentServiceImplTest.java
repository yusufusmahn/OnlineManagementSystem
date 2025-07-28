package com.lms.services;


import com.lms.data.models.*;
import com.lms.data.respositories.*;
import com.lms.dto.requests.*;
import com.lms.dto.responses.*;
import com.lms.exception.*;
import com.lms.security.CurrentUserProvider;
import com.lms.utils.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class AssignmentServiceImplTest {

    @Mock
    private AssignmentRepository assignmentRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CloudinaryService cloudinaryService;
    @Mock
    private EmailService emailService;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private AssignmentServiceImpl assignmentService;

    private User instructor;
    private Course course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        instructor = new User();
        instructor.setId(UUID.randomUUID());
        instructor.setName("Instructor");
        instructor.setEmail("instructor@example.com");
        instructor.setRole(Role.INSTRUCTOR);

        course = new Course();
        course.setId(10L);
        course.setTitle("Java Basics");
        course.setInstructor(instructor);
    }

    @Test
    void testCreateAssignment_ByInstructor_Success() {
        AssignmentRequest request = new AssignmentRequest();
        request.setTitle("Assignment 1");
        request.setDescription("Complete Chapter 1");
        request.setDueDate(LocalDateTime.now().plusDays(7));
        request.setCourseId(course.getId());

        MockMultipartFile file = new MockMultipartFile(
                "file", "assignment.pdf", "application/pdf", "Some file content".getBytes());

        when(currentUserProvider.getCurrentUser()).thenReturn(instructor);
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(cloudinaryService.uploadFile(file)).thenReturn("http://cloudinary.com/file.pdf");

        Assignment savedAssignment = Assignment.builder()
                .id(100L)
                .title("Assignment 1")
                .description("Complete Chapter 1")
                .dueDate(request.getDueDate())
                .fileUrl("http://cloudinary.com/file.pdf")
                .course(course)
                .build();

        when(assignmentRepository.save(any(Assignment.class))).thenReturn(savedAssignment);
        when(enrollmentRepository.findByCourse(course)).thenReturn(Collections.emptyList());

        AssignmentResponse response = assignmentService.createAssignment(request, file);

        assertNotNull(response);
        assertEquals("Assignment 1", response.getTitle());
        assertEquals("http://cloudinary.com/file.pdf", response.getFileUrl());
    }

    @Test
    void testCreateAssignment_ByUnauthorizedUser_ThrowsException() {
        User student = new User();
        student.setId(UUID.randomUUID());
        student.setRole(Role.STUDENT);

        AssignmentRequest request = new AssignmentRequest();
        request.setCourseId(course.getId());

        when(currentUserProvider.getCurrentUser()).thenReturn(student);
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));

        assertThrows(UnauthorizedActionException.class,
                () -> assignmentService.createAssignment(request, null));
    }

    @Test
    void testGetAssignmentsByCourse_AsStudent_Success() {
        User student = new User();
        student.setId(UUID.randomUUID());
        student.setRole(Role.STUDENT);

        Assignment assignment = Assignment.builder()
                .id(1L)
                .title("Assignment A")
                .description("Desc")
                .dueDate(LocalDateTime.now())
                .course(course)
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(student);
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(true);
        when(assignmentRepository.findByCourseId(course.getId())).thenReturn(List.of(assignment));

        List<AssignmentResponse> responses = assignmentService.getAssignmentsByCourse(course.getId());

        assertEquals(1, responses.size());
        assertEquals("Assignment A", responses.get(0).getTitle());
    }

    @Test
    void testGetAssignmentsByCourse_UnauthorizedUser_ThrowsException() {
        User stranger = new User();
        stranger.setId(UUID.randomUUID());
        stranger.setRole(Role.STUDENT); // Not enrolled

        when(currentUserProvider.getCurrentUser()).thenReturn(stranger);
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentAndCourse(stranger, course)).thenReturn(false);

        assertThrows(UnauthorizedActionException.class,
                () -> assignmentService.getAssignmentsByCourse(course.getId()));
    }
}
