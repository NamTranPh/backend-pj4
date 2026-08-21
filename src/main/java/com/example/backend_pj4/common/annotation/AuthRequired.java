// Chú thích yêu cầu người dùng phải xác thực (đăng nhập) trước khi gọi API.
package com.example.backend_pj4.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "bearerAuth")
public @interface AuthRequired {
}
