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

import com.example.backend_pj4.infrastructure.config.properties.AuthCookieProperties;
import com.example.backend_pj4.infrastructure.config.properties.JwtProperties;
import com.example.backend_pj4.infrastructure.config.properties.RedisKeyProperties;
import com.example.backend_pj4.infrastructure.database.repositories.UserJpaRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final AuthCookieProperties authCookieProperties;
    private final JwtProperties jwtProperties;
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
                String username = jwtTokenProvider.getSubjectFromToken(jwt);

                if (username != null) {
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
            }
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            log.debug("JWT expired, proceeding as anonymous: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
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

    private String getJwtFromRequest(HttpServletRequest request) {
        String cookieToken = getCookieValue(request, authCookieProperties.getAccessName());
        if (StringUtils.hasText(cookieToken)) {
            return cookieToken;
        }

        String bearerToken = request.getHeader("Authorization");
        if (jwtProperties.isEnableBearerFallback()
                && StringUtils.hasText(bearerToken)
                && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
