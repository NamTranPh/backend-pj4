package com.example.backend_pj4.infrastructure.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.backend_pj4.common.constants.enums.UserRole;

/**
 * Mặc định, lớp User của Spring Security chỉ lưu username (email), password và authorities (danh sách quyền). 
 * Lớp này được tạo ra để gắn thêm userId và tokenVersion vào bộ nhớ đệm của Spring Security.
 */
public class AppUserDetails extends User {

    private static final long serialVersionUID = 1L;

    private final transient String userId;
    private final transient int tokenVersion;

    public AppUserDetails(String userId,
                          String username,
                          String password,
                          boolean enabled,
                          boolean accountNonLocked,
                          Collection<? extends GrantedAuthority> authorities,
                          int tokenVersion) {
        super(username, password, enabled, true, true, accountNonLocked, authorities);
        this.userId = userId;
        this.tokenVersion = tokenVersion;
    }

    public String getUserId() {
        return userId;
    }

    public int getTokenVersion() {
        return tokenVersion;
    }

    /**
     * Lấy Role đọc từ authorities người dùng thay vì tra database.
     */
    public static UserRole roleOf(UserDetails principal) {
        return principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> UserRole.valueOf(authority.substring("ROLE_".length())))
                .findFirst()
                .orElse(UserRole.USER);
    }

    /**
     * Lấy userId từ thông tin đăng nhập. Dùng ở controller thay cho việc gọi thêm một use case chỉ để tra id.
     */
    public static String userIdOf(UserDetails principal) {
        if (principal instanceof AppUserDetails appUser) {
            return appUser.getUserId();
        }
        throw new IllegalStateException(
                "Principal không phải AppUserDetails: " + (principal == null ? "null" : principal.getClass()));
    }
}
