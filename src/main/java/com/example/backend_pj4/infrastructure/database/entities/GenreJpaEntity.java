package com.example.backend_pj4.infrastructure.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import com.example.backend_pj4.common.constants.enums.GenreStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "genre", indexes = {
        @Index(name = "idx_genre_deleted_at", columnList = "deleted_at"),
        @Index(name = "idx_genre_status", columnList = "status")
})
@Where(clause = "deleted_at IS NULL")
@Getter
public class GenreJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "slug", unique = true, nullable = false, length = 120)
    private String slug;

    @Column(name = "icon", length = 255)
    private String icon;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private GenreStatus status;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    private List<MovieJpaEntity> movies = new ArrayList<>();

    public GenreJpaEntity() {}

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(GenreStatus status) { this.status = status; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}

