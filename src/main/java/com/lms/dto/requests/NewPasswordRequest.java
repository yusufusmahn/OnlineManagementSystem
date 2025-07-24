// src/main/java/com/lms/dto/requests/NewPasswordRequest.java
package com.lms.dto.requests;

import lombok.Data;

@Data
public class NewPasswordRequest {
    private String token;
    private String newPassword;
}
