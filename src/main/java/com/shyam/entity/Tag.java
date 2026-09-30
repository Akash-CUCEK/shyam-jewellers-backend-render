package com.shyam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(
        name = "tag",
        uniqueConstraints = @UniqueConstraint(columnNames = "tag_name")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "tag_name", nullable = false, unique = true, length = 100)
    private String tagName;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;
}