package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.infrastructure.database.entities.CommentLikeJpaEntity;

public interface CommentLikeJpaRepository extends JpaRepository<CommentLikeJpaEntity, String> {

    boolean existsByUser_IdAndComment_Id(String userId, String commentId);

    void deleteByUser_IdAndComment_Id(String userId, String commentId);

    /**
     * Chỉ lấy cột id thay vì cả thực thể: danh sách này chỉ để bật cờ {@code isLiked}, nạp cả
     * dòng về rồi vứt đi là đọc thừa.
     */
    @Query("SELECT cl.comment.id FROM CommentLikeJpaEntity cl "
            + "WHERE cl.user.id = :userId AND cl.comment.id IN :commentIds")
    List<String> findLikedCommentIds(@Param("userId") String userId,
                                     @Param("commentIds") Collection<String> commentIds);
}
