package com.example.backend_pj4.domain.repository;

import java.util.Optional;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.RefreshToken;
import com.example.backend_pj4.domain.repository.filter.SessionFilter;
public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByTokenId(String tokenId);
    /** Các phiên đăng nhập còn hiệu lực, mỗi sessionId một dòng. */
    PageResult<RefreshToken> find(SessionFilter filter, PageQuery query);
    /** Phiên này có thuộc về người dùng này và còn hiệu lực không. */
    boolean existsActiveSession(String userId, String sessionId);
    void revokeByTokenId(String tokenId);
    void revokeAllByUserId(String userId);
    /** Thu hồi toàn bộ token của một phiên, dùng cho đăng xuất thiết bị này. */
    void revokeAllBySessionId(String sessionId);
    /** @return số row đã xoá, để job ghi log. */
    int deleteExpired();
}
