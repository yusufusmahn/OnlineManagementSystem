package com.lms.services;

import com.lms.data.models.Course;

import java.util.List;

public interface EnrollmentService {
    void enrollInCourse(String courseId);
    List<Course> getEnrolledCourses();

}
