package com.example.backend_pj4.domain.repository;

import java.util.Optional;

import com.example.backend_pj4.domain.model.Membership;

/**
 * Đăng ký gói thành viên của người dùng.
 *
 * <p>Chưa có API quản trị nào liệt kê membership nên chưa có {@code find}. Khi làm màn hình đó,
 * thêm {@code find(MembershipFilter, PageQuery)} — không dùng lại kiểu trả {@code List} không
 * phân trang.
 */
public interface MembershipRepository {
    Membership save(Membership membership);

    Optional<Membership> findById(String memberId);

    /** Gói đang còn hiệu lực của một người dùng — dùng để kiểm quyền xem phim premium. */
    Optional<Membership> findActiveByUserId(String userId);

    /** Đăng ký còn hiệu lực thuộc một gói — chặn xoá/sửa gói đang có người dùng. */
    java.util.List<Membership> findActiveByPlanId(String planId);

    void deleteById(String memberId);
}
