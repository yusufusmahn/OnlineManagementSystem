package com.lms.services;


import com.lms.dto.requests.UserRequestDTO;
import com.lms.dto.responses.UserResponseDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO dto);
    UserResponseDTO getUserById(UUID id);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO updateUser(UUID id, UserRequestDTO dto);
    void deleteUser(UUID id);
}
