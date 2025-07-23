package com.lms.utils;


import com.lms.data.models.User;
import com.lms.dto.requests.UserRequestDTO;
import com.lms.dto.responses.UserResponseDTO;

public class UserMapper {

    public static User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail().toLowerCase()); // normalize
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole()); // NEW

        return user;
    }

    public static UserResponseDTO toDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole()); //  correct direction
        return dto;
    }

}
