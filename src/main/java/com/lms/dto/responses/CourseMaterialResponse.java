package com.lms.dto.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseMaterialResponse {
    private Long id;
    private String title;
    private String type;
    private String fileUrl;
}
