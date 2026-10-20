package com.example.backend_pj4.infrastructure.database.repositories;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.infrastructure.database.entities.CommentJpaEntity;
public interface CommentJpaRepository
        extends JpaRepository<CommentJpaEntity, String>, JpaSpecificationExecutor<CommentJpaEntity> {
    @Query("SELECT c FROM CommentJpaEntity c WHERE c.rootId IN :rootIds ORDER BY c.createdAt ASC")
    List<CommentJpaEntity> findByRootIds(@Param("rootIds") Collection<String> rootIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE CommentJpaEntity c SET c.deletedAt = :now WHERE c.id IN :ids")
    void softDeleteByIds(@Param("ids") Collection<String> ids, @Param("now") LocalDateTime now);

    /** Đường lui khi Redis không ghi được; đường bình thường là {@link #recountLikeCounts}. */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE CommentJpaEntity c SET c.likeCount = c.likeCount + :delta WHERE c.id = :id")
    void addLikeCount(@Param("id") String id, @Param("delta") int delta);

    /**
     * Đếm lại từ bảng {@code comment_like} chứ không cộng dồn delta — nhờ vậy số liệu tự chữa
     * lành sau khi Redis restart làm mất phần delta chưa flush.
     *
     * <p>Native query vì JPQL không cho subquery trong mệnh đề SET của câu UPDATE.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE comment c SET c.like_count = "
            + "(SELECT COUNT(*) FROM comment_like cl WHERE cl.comment_id = c.id) "
            + "WHERE c.id IN (:ids)", nativeQuery = true)
    void recountLikeCounts(@Param("ids") Collection<String> ids);
}
