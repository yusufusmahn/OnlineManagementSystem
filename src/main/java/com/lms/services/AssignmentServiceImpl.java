package com.lms.services;

import com.lms.data.models.Assignment;
import com.lms.data.models.*;
import com.lms.data.respositories.*;
import com.lms.dto.requests.AssignmentRequest;
import com.lms.dto.responses.*;
import com.lms.exception.*;
import com.lms.utils.AssignmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final CloudinaryService cloudinaryService; // Replace if you use another storage

    @Override
    public AssignmentResponse createAssignment(AssignmentRequest request, MultipartFile file) {
        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            fileUrl = cloudinaryService.uploadFile(file); // implement this if not yet
        }

        Assignment assignment = Assignment.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .dueDate(request.getDueDate())
            .fileUrl(fileUrl)
            .course(course)
            .build();

        assignment = assignmentRepository.save(assignment);

        return AssignmentMapper.toDTO(assignment);
    }


    @Override
    public List<AssignmentResponse> getAssignmentsByCourse(Long courseId) {
        return assignmentRepository.findByCourseId(courseId)
                .stream()
                .map(AssignmentMapper::toDTO)
                .toList();
    }


}
