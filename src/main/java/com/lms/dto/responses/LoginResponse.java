package com.lms.dto.responses;

import com.lms.data.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String name;
    private String email;
    private Role role;
    private String token;
}
