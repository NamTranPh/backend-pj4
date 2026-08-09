package com.example.backend_pj4.infrastructure.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "membership_plan")
@Getter
public class MembershipPlanJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "max_devices")
    private Integer maxDevices;

    @Column(name = "can_download")
    private Boolean canDownload;

    @Column(name = "video_quality", length = 20)
    private String videoQuality;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public MembershipPlanJpaEntity() {}

    public void setId(String id) { this.id = id; }

    public MembershipPlanJpaEntity(String id, String name, BigDecimal price, Integer durationDays,
                                Integer maxDevices, Boolean canDownload, String videoQuality,
                                String description, Boolean isActive, LocalDateTime createdAt,
                                LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.durationDays = durationDays;
        this.maxDevices = maxDevices;
        this.canDownload = canDownload;
        this.videoQuality = videoQuality;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setName(String name) { this.name = name; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
    public void setMaxDevices(Integer maxDevices) { this.maxDevices = maxDevices; }
    public void setCanDownload(Boolean canDownload) { this.canDownload = canDownload; }
    public void setVideoQuality(String videoQuality) { this.videoQuality = videoQuality; }
    public void setDescription(String description) { this.description = description; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}

