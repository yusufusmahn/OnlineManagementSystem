package com.lms.data.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User student;

    @ManyToOne(optional = false)
    private Course course;

    private LocalDateTime enrolledAt;

    @PrePersist
    public void setEnrolledAt() {
        this.enrolledAt = LocalDateTime.now();
    }
}
