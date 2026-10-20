// Job gộp số lượt thích: Redis giữ phần chênh lệch cho nhanh, định kỳ gộp xuống cột
// comment.like_count để số liệu vẫn còn sau khi Redis bị xoá hoặc khởi động lại.
package com.example.backend_pj4.infrastructure.jobs;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.port.in.comment.FlushLikeCountsUseCase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CommentLikeFlushJob {

    private final FlushLikeCountsUseCase flushLikeCountsUseCase;

    /**
     * Chặn hai lượt chạy chồng lên nhau khi một lượt chạy lâu hơn chu kỳ — giống cách
     * {@code PaymentReconcileJob} đang làm.
     */
    private final AtomicBoolean running = new AtomicBoolean(false);

    public CommentLikeFlushJob(FlushLikeCountsUseCase flushLikeCountsUseCase) {
        this.flushLikeCountsUseCase = flushLikeCountsUseCase;
    }

    @Scheduled(fixedRateString = "${app.cache.like-flush-interval-ms:300000}")
    public void flushLikeCounts() {
        if (!running.compareAndSet(false, true)) {
            log.debug("Comment like flush job dang chay, bo qua luot nay");
            return;
        }

        try {
            int updated = flushLikeCountsUseCase.execute();
            if (updated > 0) {
                log.info("Da gop so luot thich cho {} binh luan", updated);
            }
        } catch (Exception e) {
            log.warn("Gop so luot thich that bai", e);
        } finally {
            running.set(false);
        }
    }
}
