package com.example.backend_pj4.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.backend_pj4.domain.enums.MembershipStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "profile_picture", length = 500)
    private String profilePicture;

    @Column(name = "phone", nullable = false, unique = true, length = 20)
    private String phone;

    @Column(name = "address")
    @Lob
    private String address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_status")
    private MembershipStatus membershipStatus = MembershipStatus.FREE;

    @Column(name = "membership_expiry_date")
    private LocalDate membershipExpiryDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    // private List<Comment> comments;

    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    // private List<Rating> ratings;

    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    // private List<Favorite> favorites;

    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    // private List<HistoryWatching> watchingHistory;
}