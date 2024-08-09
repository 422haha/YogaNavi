package com.yoga.backend.article;

import com.yoga.backend.common.entity.Article;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 게시글(공지사항) 리포지토리 인터페이스
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * 특정 사용자가 작성한 게시글 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 게시글 목록
     */
    List<Article> findByUserId(int userId);

    /**
     * 내용으로 게시글을 조회합니다.
     *
     * @param content 게시글 내용
     * @return 게시글 목록
     */
    List<Article> findByContent(String content);

    @Query("SELECT a FROM Article a JOIN FETCH a.user WHERE a.user.id = :userId")
    List<Article> findByUserIdWithUser(@Param("userId") int userId);

    @Query("SELECT a FROM Article a JOIN FETCH a.user")
    List<Article> findAllWithUser();

    @Query("SELECT a FROM Article a JOIN FETCH a.user WHERE a.articleId = :id")
    Optional<Article> findByIdWithUser(@Param("id") Long id);
}
