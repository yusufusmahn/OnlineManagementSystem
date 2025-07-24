package com.lms.dto.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInstructorRequestDTO {
    private String name;
    private String email;
    private String password;
}
