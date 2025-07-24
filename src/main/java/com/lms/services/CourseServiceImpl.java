package com.lms.services;


import com.lms.data.models.Course;
import com.lms.data.models.Enrollment;
import com.lms.data.respositories.CourseRepository;
import com.lms.data.respositories.EnrollmentRepository;
import com.lms.dto.requests.CourseRequestDTO;
import com.lms.data.models.User;
import com.lms.dto.requests.CourseUpdateRequest;
import com.lms.dto.responses.CourseResponseDTO;
import com.lms.dto.responses.UserResponse;
import com.lms.exception.NotFoundException;
import com.lms.exception.ResourceNotFoundException;
import com.lms.exception.UnauthorizedException;
import com.lms.security.CurrentUserProvider;
import com.lms.utils.CourseMapper;
import com.lms.utils.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.lms.data.models.Role.ADMIN;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CurrentUserProvider currentUserProvider;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO dto) {
        User currentUser = currentUserProvider.getCurrentUser();

        if (currentUser.getRole() != ADMIN) {
            throw new UnauthorizedException("Only admins can create courses.");
        }

        //  Pass current user as instructor
        Course course = CourseMapper.toCourse(dto, currentUser);
        courseRepository.save(course);
        return CourseMapper.toResponseDTO(course);
    }


    @Override
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(CourseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateCourse(String courseId, CourseUpdateRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();
        Long id = Long.parseLong(courseId);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found"));

        if (!course.getInstructor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only update your own courses.");
        }

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setPrice(request.getPrice());

        courseRepository.save(course);
    }


    @Override
    public void deleteCourse(String courseId) {
        User currentUser = currentUserProvider.getCurrentUser();
        Long id = Long.parseLong(courseId);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found"));

        if (!course.getInstructor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only delete your own courses.");
        }

        courseRepository.delete(course);
    }

    @Override
    public List<UserResponse> getEnrolledStudents(Long courseId) {
        User currentUser = currentUserProvider.getCurrentUser();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!course.getInstructor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the course instructor can view enrolled students.");
        }

        List<Enrollment> enrollments = enrollmentRepository.findByCourse(course);

        return enrollments.stream()
                .map(enrollment -> UserMapper.toDTO(enrollment.getStudent()))
                .collect(Collectors.toList());
    }




}

