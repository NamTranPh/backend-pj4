package com.example.backend_pj4.infrastructure.health;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.infrastructure.config.properties.MinioProperties;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * In bảng tóm tắt sau khi app khởi động xong: địa chỉ truy cập app + trạng thái
 * kết nối MySQL / Redis / MinIO. Chạy ở ApplicationReadyEvent nên nằm CUỐI log.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StartupConnectivityChecker {

    private final DataSource dataSource;
    private final RedisConnectionFactory redisConnectionFactory;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${springdoc.swagger-ui.path:/swagger-ui.html}")
    private String swaggerPath;

    @EventListener(ApplicationReadyEvent.class)
    public void checkConnections() {
        String base = "http://localhost:" + serverPort + contextPath;

        log.info("""

                ================= BACKEND ĐÃ SẴN SÀNG =================
                  App        : {}
                  Swagger UI : {}
                  Health     : {}
                  ---------------------------------------------------
                  MySQL : {}
                  Redis : {}
                  MinIO : {}
                =======================================================""",
                base,
                base + swaggerPath,
                base + "/actuator/health",
                checkMysql(),
                checkRedis(),
                checkMinio());
    }

    private String checkMysql() {
        try (Connection connection = dataSource.getConnection()) {
            // Bỏ phần query param (?useSSL=...) cho gọn.
            String url = connection.getMetaData().getURL();
            int q = url.indexOf('?');
            if (q > 0) {
                url = url.substring(0, q);
            }
            return "OK    " + url;
        } catch (Exception e) {
            return "FAIL  " + e.getMessage();
        }
    }

    private String checkRedis() {
        RedisConnection connection = null;
        try {
            connection = redisConnectionFactory.getConnection();
            String pong = connection.ping();
            return "OK    " + redisHost() + " (ping=" + pong + ")";
        } catch (Exception e) {
            return "FAIL  " + redisHost() + " (" + e.getMessage() + ")";
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    private String checkMinio() {
        try {
            // Thao tác nhẹ, không tạo/xoá gì; chỉ để xác nhận nối được.
            minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioProperties.getBucketRaw())
                    .build());
            return "OK    " + minioProperties.getEndpoint();
        } catch (Exception e) {
            return "FAIL  " + minioProperties.getEndpoint() + " (" + e.getMessage() + ")";
        }
    }

    private String redisHost() {
        if (redisConnectionFactory instanceof LettuceConnectionFactory lettuce) {
            return lettuce.getHostName() + ":" + lettuce.getPort();
        }
        return "redis";
    }
}
