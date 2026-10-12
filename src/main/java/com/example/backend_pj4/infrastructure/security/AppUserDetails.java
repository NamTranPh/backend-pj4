package com.example.backend_pj4.infrastructure.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * UserDetails mang thêm {@code tokenVersion} để {@link JwtAuthenticationFilter} đối chiếu với
 * claim {@code ver} của access token mà không phải chạy thêm một truy vấn nữa.
 *
 * <p>Nếu không có trường này, filter sẽ phải nạp lại User ở tầng domain chỉ để đọc một con số —
 * trong khi nó vốn đã nạp UserDetails cho mỗi request rồi.
 */
public class AppUserDetails extends User {

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

    /**
     * ID người dùng, mang sẵn để controller không phải tra ngược email -> id.
     *
     * <p>{@code UserDetails} chuẩn chỉ có {@code username} (ở đây là email), trong khi mọi use case
     * nhận {@code userId}. Thiếu trường này, mỗi controller phải gọi thêm một truy vấn
     * {@code getCurrentUserUseCase} chỉ để đọc {@code id} — tức là chạy lại đúng câu query mà
     * {@link CustomUserDetailsService} vừa chạy xong ở filter.
     */
    public String getUserId() {
        return userId;
    }

    public int getTokenVersion() {
        return tokenVersion;
    }

    /**
     * Role đọc từ authorities thay vì tra database. Spring Security đã mang sẵn nó dưới dạng
     * {@code ROLE_ADMIN} / {@code ROLE_USER} từ lúc filter dựng principal.
     */
    public static com.example.backend_pj4.common.constants.enums.UserRole roleOf(
            org.springframework.security.core.userdetails.UserDetails principal) {
        return principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> com.example.backend_pj4.common.constants.enums.UserRole
                        .valueOf(authority.substring("ROLE_".length())))
                .findFirst()
                .orElse(com.example.backend_pj4.common.constants.enums.UserRole.USER);
    }

    /**
     * Lấy userId từ principal. Dùng ở controller thay cho việc gọi thêm một use case chỉ để tra id.
     *
     * @throws IllegalStateException nếu principal không phải AppUserDetails — nghĩa là có nhánh
     *         xác thực khác đang dựng UserDetails kiểu khác, cần sửa ở đó chứ không nuốt lỗi.
     */
    public static String userIdOf(org.springframework.security.core.userdetails.UserDetails principal) {
        if (principal instanceof AppUserDetails appUser) {
            return appUser.getUserId();
        }
        throw new IllegalStateException(
                "Principal không phải AppUserDetails: " + (principal == null ? "null" : principal.getClass()));
    }
}
