package com.example.backend_pj4.infrastructure.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.backend_pj4.infrastructure.config.properties.RedisKeyProperties;
import com.example.backend_pj4.infrastructure.database.repositories.UserJpaRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Mọi endpoint dưới prefix này thuộc kênh CMS, chỉ token có ch=admin mới đi qua. */
    private static final String ADMIN_PATH_PREFIX = "/api/v1/admin/";

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final UserJpaRepository userJpaRepository;
    private final StringRedisTemplate redisTemplate;
    private final RedisKeyProperties redisKeyProperties;

    private static final long ACTIVE_THROTTLE_MINUTES = 5;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticate(request, jwt);
            }
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            log.debug("JWT expired, proceeding as anonymous: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request, String jwt) {
        // Chỉ access token mới xác thực được request. Refresh token (sống 30 ngày, nằm
        // trong cookie) gửi qua header Authorization sẽ bị loại ở đây.
        if (!JwtTokenProvider.TOKEN_TYPE_ACCESS.equals(jwtTokenProvider.getAccessTokenTypeFromToken(jwt))) {
            log.debug("Rejected non-access token on {}", request.getRequestURI());
            return;
        }

        // Cô lập kênh: token phát ở cửa người dùng không mở được CMS, kể cả khi tài khoản
        // đó là ADMIN. Token phát ở cửa admin dùng được cả hai kênh.
        String channel = jwtTokenProvider.getChannelFromToken(jwt);
        if (request.getRequestURI().startsWith(ADMIN_PATH_PREFIX)
                && !JwtTokenProvider.CHANNEL_ADMIN.equals(channel)) {
            log.debug("Rejected channel={} token on admin path {}", channel, request.getRequestURI());
            return;
        }

        String username = jwtTokenProvider.getSubjectFromToken(jwt);
        if (username == null) {
            return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtTokenProvider.validateToken(jwt, userDetails)
                && userDetails.isAccountNonLocked()
                && userDetails.isEnabled()) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            touchLastActive(username);
        }
    }

    private void touchLastActive(String email) {
        try {
            String key = redisKeyProperties.getPrefix() + ":last-active:" + email;
            Boolean absent = redisTemplate.opsForValue()
                    .setIfAbsent(key, "1", ACTIVE_THROTTLE_MINUTES, TimeUnit.MINUTES);
            if (Boolean.TRUE.equals(absent)) {
                userJpaRepository.updateLastActiveAt(email, LocalDateTime.now());
            }
        } catch (Exception e) {
            log.debug("Failed to update lastActiveAt for {}: {}", email, e.getMessage());
        }
    }

    /**
     * Access token chỉ đến từ header Authorization. Không đọc cookie: access token không
     * còn được ghi vào cookie, và một cookie cũ sót lại sẽ che mất header của client.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
