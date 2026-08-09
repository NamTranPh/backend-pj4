// Lớp cơ sở (Abstract Class) cho tất cả Service cung cấp Logger tự động định nghĩa theo Class.
package com.example.backend_pj4.common.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseService {
    protected final Logger logger;

    protected BaseService() {
        this.logger = LoggerFactory.getLogger(getClass());
    }

    protected BaseService(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }
}
