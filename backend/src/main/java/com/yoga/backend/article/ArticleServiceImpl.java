package com.yoga.backend.article;

import com.yoga.backend.common.entity.Article;
import com.yoga.backend.common.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 게시글(공지사항) 서비스 구현 클래스
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final S3Service s3Service;

    /**
     * ArticleServiceImpl 생성자
     *
     * @param articleRepository ArticleRepository 객체
     * @param s3Service         S3Service 객체
     */
    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, S3Service s3Service) {
        this.articleRepository = articleRepository;
        this.s3Service = s3Service;
    }

    /**
     * 게시글을 저장합니다.
     *
     * @param article 저장할 게시글 객체
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
     * 특정 사용자의 모든 게시글을 조회
     *
     * @param userId 사용자 ID
     * @return 게시글 리스트
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Article> getArticlesByUserId(int userId) {
        return articleRepository.findByUserIdWithUser(userId);
    }

    /**
     * 특정 게시글을 ID로 조회
     *
     * @param id 게시글 ID
     * @return 게시글 Optional 객체
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findByIdWithUser(id);
    }

    /**
     * 게시글 업데이트
     *
     * @param articleId     게시글 ID
     * @param newContent    새로운 내용
     * @param newImage      새로운 이미지 URL
     * @param newImageSmall 새로운 작은 이미지 URL
     * @return 업데이트된 게시글 객체
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Article updateArticle(Long articleId, String newContent, String newImage,
        String newImageSmall) {
        try {
            Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. ID: " + articleId));

            // 이전 이미지 키 저장
            String oldImage = article.getImage();
            String oldImageSmall = article.getImageUrlSmall();

            // 새 이미지 설정
            article.setContent(newContent);
            article.setImage(newImage);
            article.setImageUrlSmall(newImageSmall);
            article.setUpdatedAt(LocalDateTime.now());

            // 이전 이미지 삭제
            if (oldImage != null && !oldImage.isEmpty() && !oldImage.equals(newImage)) {
                s3Service.deleteFile(oldImage);
            }
            if (oldImageSmall != null && !oldImageSmall.isEmpty() && !oldImageSmall.equals(
                newImageSmall)) {
                s3Service.deleteFile(oldImageSmall);
            }

            return articleRepository.save(article);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("게시글 업데이트 중 충돌이 발생했습니다. 다시 시도해 주세요.", e);
        } catch (Exception e) {
            throw new RuntimeException("게시글 업데이트 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 게시글 삭제
     *
     * @param id 삭제할 게시글 ID
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteArticle(Long id) {
        try {
            Optional<Article> articleOpt = articleRepository.findById(id);
            if (articleOpt.isPresent()) {
                Article article = articleOpt.get();
                s3Service.deleteFile(article.getImage());
                s3Service.deleteFile(article.getImageUrlSmall());
                articleRepository.deleteById(id);
            } else {
                throw new RuntimeException("게시글을 찾을 수 없습니다.");
            }
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("게시글 삭제 중 충돌이 발생했습니다. 다시 시도해 주세요.", e);
        }
    }

    /**
     * 특정 내용이 포함된 게시글 조회
     *
     * @param content 검색할 내용
     * @return 게시글 리스트
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Article> findByContent(String content) {
        return articleRepository.findByContent(content);
    }
}
