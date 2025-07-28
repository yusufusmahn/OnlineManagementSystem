package com.lms.services;

import com.lms.data.models.*;
import com.lms.data.respositories.*;
import com.lms.dto.requests.*;
import com.lms.dto.responses.*;
import com.lms.exception.*;
import com.lms.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseMaterialServiceImpl implements CourseMaterialService {

    private final CourseMaterialRepository materialRepository;
    private final CourseRepository courseRepository;
    private final CurrentUserProvider currentUserProvider;
    private final CloudinaryService cloudinaryService;

    @Override
    public CourseMaterialResponse uploadMaterial(CourseMaterialUploadRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!course.getInstructor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the instructor can upload materials to this course.");
        }

        String fileUrl;
        try {
            fileUrl = cloudinaryService.uploadFile(request.getFile());
        } catch (Exception e) {
            throw new FileUploadException("File upload failed: " + e.getMessage());
        }

        CourseMaterial material = CourseMaterial.builder()
                .title(request.getTitle())
                .type(request.getType())
                .fileUrl(fileUrl)
                .course(course)
                .build();

        CourseMaterial saved = materialRepository.save(material);

        return CourseMaterialResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .type(saved.getType())
                .fileUrl(saved.getFileUrl())
                .build();
    }

    @Override
    public List<CourseMaterialResponse> getMaterialsByCourse(Long courseId) {
        List<CourseMaterial> materials = materialRepository.findByCourseId(courseId);

        return materials.stream()
                .map(m -> CourseMaterialResponse.builder()
                        .id(m.getId())
                        .title(m.getTitle())
                        .type(m.getType())
                        .fileUrl(m.getFileUrl())
                        .build())
                .collect(Collectors.toList());
    }
}
