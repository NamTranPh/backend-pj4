// Lớp cơ sở (Abstract Class) cho tất cả Controller cung cấp sẵn SLF4J logger.
package com.example.backend_pj4.common.base;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseController {
    protected void info(String message) {
        log.info(message);
    }
}
