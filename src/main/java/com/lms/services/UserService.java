package com.lms.services;


import com.lms.dto.requests.CreateInstructorRequestDTO;
import com.lms.dto.requests.UserRequestDTO;
import com.lms.dto.responses.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse createUser(UserRequestDTO dto);
    UserResponse getUserById(UUID id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(UUID id, UserRequestDTO dto);
    void deleteUser(UUID id);
    UserResponse createInstructor(CreateInstructorRequestDTO dto);
    void deleteInstructor(UUID instructorId);
    List<UserResponse> getAllInstructors();

}
