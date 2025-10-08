package com.example.backend_pj4.infrastructure.databases.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "membership_plan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPlanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "plan_id", updatable = false, nullable = false)
    private String planId;

    @Column(name = "plan_name", nullable = false, unique = true, length = 100)
    private String planName;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "max_devices")
    private Integer maxDevices = 1;

    @Column(name = "can_download")
    private Boolean canDownload = false;

    @Column(name = "video_quality", length = 20)
    private String videoQuality = "HD";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY)
    private List<MembershipEntity> memberships;
}