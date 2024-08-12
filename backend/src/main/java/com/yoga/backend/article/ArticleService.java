package com.yoga.backend.article;

import java.util.List;
import java.util.Optional;

public interface ArticleService {

    /**
     * 게시글을 저장
     *
     * @param userId     사용자 ID
     * @param articleDto 저장할 게시글 DTO
     * @return 생성된 ArticleDto 객체
     */
    ArticleDto saveArticle(int userId, ArticleDto articleDto);

    /**
     * 특정 사용자가 작성한 게시글을 조회
     *
     * @param userId 사용자 ID
     * @return 게시글 DTO 리스트
     */
    List<ArticleDto> getArticlesByUserId(int userId);

    /**
     * 게시글 ID로 특정 게시글을 조회
     *
     * @param id 게시글 ID
     * @return 게시글 DTO Optional 객체
     */
    Optional<ArticleDto> getArticleById(Long id);

    /**
     * 게시글 업데이트
     *
     * @param userId     사용자 ID
     * @param articleId  게시글 ID
     * @param articleDto 업데이트할 게시글 정보
     * @return 업데이트된 게시글 DTO 객체
     */
    ArticleDto updateArticle(int userId, Long articleId, ArticleDto articleDto);

    /**
     * 게시글 삭제
     *
     * @param userId 사용자 ID
     * @param id     삭제할 게시글 ID
     * @return 삭제 성공 여부
     */
    boolean deleteArticle(int userId, Long id);

    /**
     * 내용으로 게시글을 조회
     *
     * @param content 게시글 내용
     * @return 게시글 DTO 리스트
     */
    List<ArticleDto> findByContent(String content);
}
