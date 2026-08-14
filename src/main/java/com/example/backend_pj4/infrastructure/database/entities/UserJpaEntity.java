package com.example.backend_pj4.infrastructure.database.entities;

import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.constants.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user", indexes = {
        @Index(name = "idx_user_account_status", columnList = "account_status"),
        @Index(name = "idx_user_deleted_at", columnList = "deleted_at")
})
@Where(clause = "deleted_at IS NULL")
@Getter
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "profile_url", length = 500)
    private String profileUrl;

    @Column(name = "phone", unique = true, length = 20)
    private String phone;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "email_verified")
    private Boolean emailVerified;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status")
    private AccountStatus accountStatus;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts;

    @Column(name = "first_failure_at")
    private LocalDateTime firstFailureAt;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<CommentJpaEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<RatingJpaEntity> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ListFavoriteJpaEntity> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<HistoryWatchingJpaEntity> watchingHistory = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<MembershipJpaEntity> memberships = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<MovieJpaEntity> createdMovies = new ArrayList<>();

    public UserJpaEntity() {}

    public void setId(String id) { this.id = id; }

    public UserJpaEntity(String id, String email, String password, String name, String profileUrl,
                      String phone, String address, UserRole role, Boolean emailVerified,
                      AccountStatus accountStatus, LocalDateTime deletedAt,
                      LocalDateTime createdAt, LocalDateTime updatedAt,
                      List<CommentJpaEntity> comments, List<RatingJpaEntity> ratings,
                      List<ListFavoriteJpaEntity> favorites, List<HistoryWatchingJpaEntity> watchingHistory,
                      List<MembershipJpaEntity> memberships, List<MovieJpaEntity> createdMovies) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.profileUrl = profileUrl;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.emailVerified = emailVerified;
        this.accountStatus = accountStatus;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.comments = comments != null ? comments : new ArrayList<>();
        this.ratings = ratings != null ? ratings : new ArrayList<>();
        this.favorites = favorites != null ? favorites : new ArrayList<>();
        this.watchingHistory = watchingHistory != null ? watchingHistory : new ArrayList<>();
        this.memberships = memberships != null ? memberships : new ArrayList<>();
        this.createdMovies = createdMovies != null ? createdMovies : new ArrayList<>();
    }

    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setProfileUrl(String profileUrl) { this.profileUrl = profileUrl; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setRole(UserRole role) { this.role = role; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }
    public void setAccountStatus(AccountStatus accountStatus) { this.accountStatus = accountStatus; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
    public void setFirstFailureAt(LocalDateTime firstFailureAt) { this.firstFailureAt = firstFailureAt; }
    public void setLockedUntil(LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}

