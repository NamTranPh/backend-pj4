package com.example.backend_pj4.infrastructure.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "genre", indexes = {
        @Index(name = "idx_genre_deleted_at", columnList = "deleted_at")
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

    @Column(name = "icon", length = 255)
    private String icon;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

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

    public GenreJpaEntity(String id, String name, String icon, String description,
                       LocalDateTime deletedAt, LocalDateTime createdAt, LocalDateTime updatedAt,
                       List<MovieJpaEntity> movies) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.movies = movies != null ? movies : new ArrayList<>();
    }

    public void setName(String name) { this.name = name; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setDescription(String description) { this.description = description; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}

