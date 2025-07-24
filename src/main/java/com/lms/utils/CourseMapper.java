package com.lms.utils;

import com.lms.data.models.Course;
import com.lms.data.models.User;
import com.lms.dto.requests.CourseRequestDTO;
import com.lms.dto.responses.CourseResponseDTO;

import java.time.format.DateTimeFormatter;

import com.lms.data.models.User;

public class CourseMapper {

    // You must pass the actual User (instructor) when mapping
    public static Course toCourse(CourseRequestDTO dto, User instructor) {
        return Course.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .instructor(instructor)
                .price(dto.getPrice())
                .build();
    }

    public static CourseResponseDTO toResponseDTO(Course course) {
        return CourseResponseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .instructorName(course.getInstructor().getName()) //  fixed
                .price(course.getPrice())
                .createdAt(course.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .build();
    }
}


