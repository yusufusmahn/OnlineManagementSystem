package com.lms.services;

import com.lms.dto.requests.CourseMaterialUploadRequest;
import com.lms.dto.responses.CourseMaterialResponse;

import java.util.List;

public interface CourseMaterialService {
    CourseMaterialResponse uploadMaterial(CourseMaterialUploadRequest request);
    List<CourseMaterialResponse> getMaterialsByCourse(Long courseId);
}
