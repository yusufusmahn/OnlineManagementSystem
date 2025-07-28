package com.lms.data.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileUrl;

    private String title;

    private String type; // e.g., "video", "pdf", etc.

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
