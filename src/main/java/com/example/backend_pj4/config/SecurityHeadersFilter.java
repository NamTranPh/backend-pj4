package com.example.backend_pj4.config;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SecurityHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Cache control
        response.setHeader("Cache-Control", "no-cache,no-store,max-age=0,must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        // Security headers
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "0");

        // Keep-alive (Spring Boot tự handle connection, nhưng có thể set timeout nếu cần)
        response.setHeader("Connection", "keep-alive");
        response.setHeader("Keep-Alive", "timeout=60");

        // CORS/ vary headers
        response.setHeader("Vary", "Origin,Access-Control-Request-Method,Access-Control-Request-Headers");

        filterChain.doFilter(request, response);
    }
}