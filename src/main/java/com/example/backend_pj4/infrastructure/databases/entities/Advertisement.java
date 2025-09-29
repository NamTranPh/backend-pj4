package com.example.backend_pj4.infrastructure.databases.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.backend_pj4.domain.enums.AdType;

@Entity
@Table(name = "advertisement",
    indexes = {
        @Index(name = "idx_date_range", columnList = "start_date, end_date"),
        @Index(name = "idx_ad_type", columnList = "ad_type")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ad_id")
    private Integer adId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "click_url", length = 500)
    private String clickUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "ad_type")
    private AdType adType = AdType.BANNER;

    @Column(name = "position", length = 50, columnDefinition = "VARCHAR(50) COMMENT 'Vị trí hiển thị'")
    private String position;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "click_count")
    private Integer clickCount = 0;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}