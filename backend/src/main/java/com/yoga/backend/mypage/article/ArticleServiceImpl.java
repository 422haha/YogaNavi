package com.yoga.backend.mypage.article;

import com.yoga.backend.common.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void saveArticle(Article article) {
        try {
            articleRepository.save(article);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("게시글 저장 중 충돌이 발생했습니다. 다시 시도해 주세요.", e);
        }
    }

    /**
     * 모든 게시글을 조회합니다.
     *
     * @return 게시글 목록
     */
    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<Article> getArticlesByUserId(int userId) {
        return articleRepository.findByUserId(userId);
    }

    /**
     * 게시글 ID로 특정 게시글을 조회합니다.
     *
     * @param id 게시글 ID
     * @return 게시글
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    /**
     * 게시글을 업데이트합니다.
     *
     * @param articleId  게시글 ID
     * @param newContent 새로운 게시글 내용
     * @return 업데이트된 게시글
     */
    @Override
    @Transactional
    public Article updateArticle(Long articleId, String newContent) {
        try {
            Optional<Article> optionalArticle = articleRepository.findById(articleId);

            if (optionalArticle.isPresent()) {
                Article article = optionalArticle.get();
                article.setContent(newContent);
                return articleRepository.save(article);
            } else {
                throw new RuntimeException("게시글을 찾을 수 없습니다.");
            }
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("게시글 업데이트 중 충돌이 발생했습니다. 다시 시도해 주세요.", e);
        }
    }

    /**
     * 게시글을 삭제합니다.
     *
     * @param id 삭제할 게시글 ID
     */
    @Override
    @Transactional
    public void deleteArticle(Long id) {
        try {
            articleRepository.deleteById(id);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("게시글 삭제 중 충돌이 발생했습니다. 다시 시도해 주세요.", e);
        }
    }

    /**
     * 내용으로 게시글을 조회합니다.
     *
     * @param content 게시글 내용
     * @return 게시글 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<Article> findByContent(String content) {
        return articleRepository.findByContent(content);
    }
}
