package com.lms.services;



import com.lms.data.models.Course;
import com.lms.data.models.Enrollment;
import com.lms.dto.requests.CourseRequestDTO;
import com.lms.dto.requests.CourseUpdateRequest;
import com.lms.dto.responses.CourseResponseDTO;
import com.lms.dto.responses.UserResponse;

import java.util.List;

public interface CourseService {
    CourseResponseDTO createCourse(CourseRequestDTO dto);
    List<CourseResponseDTO> getAllCourses();
    // CourseService.java
    void updateCourse(String courseId, CourseUpdateRequest request);
    // CourseService.java
    void deleteCourse(String courseId);
    List<UserResponse> getEnrolledStudents(Long courseId);


}
