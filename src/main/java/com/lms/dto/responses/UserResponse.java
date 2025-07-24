package com.lms.dto.responses;

import com.lms.data.models.Role;
import lombok.Data;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private Role role;

}
