//Domain Entity -> lớp triển khai -> Usecase -> dto -> controller

package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.entities.User;


public interface UserRepository {
    // Basic CRUD
    User save(User user);
    Optional<User> findById(String userId);
    List<User> findAll();
    void deleteById(String userId);
    boolean existsById(String userId);
    
    // Business queries
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findByEmailOrPhone(String identifier);
    
    // Validation queries
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    
    // Membership queries
    List<User> findByMembershipStatus(String membershipStatus);
    List<User> findExpiredMemberships();
    
    // Admin queries
    List<User> findByRole(String roleId);
    List<User> findActiveUsers();
    List<User> findInactiveUsers();
}
