package com.lms.utils;


import com.lms.data.models.User;
import com.lms.dto.requests.UserRequestDTO;
import com.lms.dto.responses.UserResponse;

public class UserMapper {

    public static User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail().toLowerCase());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());

        return user;
    }

    public static UserResponse toDTO(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }



}
