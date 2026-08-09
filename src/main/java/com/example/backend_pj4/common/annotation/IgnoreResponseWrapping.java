// Annotation dùng để bỏ qua việc tự động bọc dữ liệu phản hồi (Response Envelope Wrapping) cho Controller Method/Class.
package com.example.backend_pj4.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreResponseWrapping {
}
