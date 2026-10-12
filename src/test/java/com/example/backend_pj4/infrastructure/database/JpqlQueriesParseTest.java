package com.example.backend_pj4.infrastructure.database;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import jakarta.persistence.EntityManagerFactory;

/**
 * Cổng chặn cú pháp JPQL, KHÔNG cần database chạy.
 *
 * <p>Hibernate phân tích mọi {@code @Query} lúc dựng SessionFactory. Sai cú pháp, sai tên thuộc
 * tính hay sai tên tham số đều làm context không lên được, nên test này đỏ ngay.
 *
 * <p>{@code allow_jdbc_metadata_access=false} cho phép dựng SessionFactory mà không mở kết nối,
 * nên gate này chạy được cả khi MySQL của máy dev đang tắt. Nó <b>không</b> thay thế test chạm
 * DB thật: hành vi của câu lệnh trên MySQL vẫn phải kiểm riêng.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect",
        "spring.jpa.properties.hibernate.boot.allow_jdbc_metadata_access=false",
        "spring.sql.init.mode=never"
})
class JpqlQueriesParseTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void moiCauJpqlTrongRepositoryDeuPhanTichDuoc() {
        assertThat(entityManagerFactory).isNotNull();
    }
}
