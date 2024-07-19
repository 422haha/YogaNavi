package com.yoga.backend.mypage.article;

import com.yoga.backend.common.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 게시글(공지사항) 서비스 구현 클래스
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * 게시글을 저장합니다.
     *
     * @param article 저장할 게시글
     */
    @Override
    public void saveArticle(Article article) {
        articleRepository.save(article);
    }

    /**
     * 모든 게시글을 조회합니다.
     *
     * @return 게시글 목록
     */
    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    /**
     * 특정 사용자가 작성한 게시글을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 게시글 목록
     */
    @Override
    public List<Article> getArticlesByUserId(int userId) { // userId 타입을 int로 수정
        return articleRepository.findByUserId(userId);
    }

    /**
     * 게시글 ID로 특정 게시글을 조회합니다.
     *
     * @param id 게시글 ID
     * @return 게시글
     */
    @Override
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    /**
     * 게시글을 삭제합니다.
     *
     * @param id 삭제할 게시글 ID
     */
    @Override
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    /**
     * 제목으로 게시글을 조회합니다.
     *
     * @param title 게시글 제목
     * @return 게시글 목록
     */
    @Override
    public List<Article> findByTitle(String title) {
        return articleRepository.findByTitle(title); // 추가된 메서드 구현
    }
}
