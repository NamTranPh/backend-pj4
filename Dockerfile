# syntax=docker/dockerfile:1

# ========== STAGE 1: BUILD ==========
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Cache dependency trước để build lần sau nhanh hơn
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ========== STAGE 2: RUN ==========
# Dùng base Ubuntu (jammy) để cài FFmpeg bằng apt giống media-prc của NhatAnh
FROM eclipse-temurin:21-jre-jammy AS runner
WORKDIR /app

# FFmpeg + ffprobe cho transcode HLS; dumb-init để xử lý signal PID 1
RUN apt-get update \
  && apt-get install -y --no-install-recommends ffmpeg ca-certificates dumb-init \
  && rm -rf /var/lib/apt/lists/*

# Copy jar đã build từ stage builder
COPY --from=builder /app/target/*.jar app.jar

# Thư mục tạm cho transcode + chạy bằng user không phải root
RUN useradd -r -u 1001 -g root appuser \
  && mkdir -p /tmp/media-processing \
  && chown -R appuser:root /app /tmp/media-processing
USER appuser

# App chạy ở SERVER_PORT=3004 (context-path /api)
EXPOSE 3004

ENTRYPOINT ["dumb-init", "--"]
CMD ["java", "-jar", "app.jar"]
