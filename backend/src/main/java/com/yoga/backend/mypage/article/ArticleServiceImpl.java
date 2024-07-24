package com.yoga.backend.mypage.article;

import com.yoga.backend.common.entity.Article;
import com.yoga.backend.common.awsS3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 게시글(공지사항) 서비스 구현 클래스
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    private static final String S3_BASE_URL = "https://yoga-navi.s3.ap-northeast-2.amazonaws.com/";
    private static final long URL_EXPIRATION_SECONDS = 86400; // 24 hours

    private final ArticleRepository articleRepository;
    private final S3Service s3Service;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, S3Service s3Service) {
        this.articleRepository = articleRepository;
        this.s3Service = s3Service;
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
        List<Article> articles = articleRepository.findAll();
        return applyPresignedUrlsAsync(articles);
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
        List<Article> articles = articleRepository.findByUserId(userId);
        return applyPresignedUrlsAsync(articles);
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
        Optional<Article> articleOpt = articleRepository.findById(id);
        return articleOpt.map(
            article -> applyPresignedUrlsAsync(Collections.singletonList(article)).get(0));
    }

    /**
     * 게시글을 업데이트합니다.
     *
     * @param articleId  게시글 ID
     * @param newContent 새로운 게시글 내용
     * @param newImage   새로운 이미지 URL
     * @return 업데이트된 게시글
     */
    @Override
    @Transactional
    public Article updateArticle(Long articleId, String newContent, String newImage) {
        try {
            Optional<Article> optionalArticle = articleRepository.findById(articleId);

            if (optionalArticle.isPresent()) {
                Article article = optionalArticle.get();
                article.setContent(newContent);

                // 이미지가 변경된 경우 기존 이미지 삭제
                if (!article.getImage().equals(newImage)) {
                    String oldImage = article.getImage();
                    article.setImage(newImage);
                    s3Service.deleteFile(oldImage);
                }

                Article updatedArticle = articleRepository.save(article);
                return applyPresignedUrlsAsync(Collections.singletonList(updatedArticle)).get(0);
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
            Optional<Article> articleOpt = articleRepository.findById(id);
            if (articleOpt.isPresent()) {
                Article article = articleOpt.get();
                // S3에서 이미지 파일 삭제
                s3Service.deleteFile(article.getImage());
                articleRepository.deleteById(id);
            } else {
                throw new RuntimeException("게시글을 찾을 수 없습니다.");
            }
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
        List<Article> articles = articleRepository.findByContent(content);
        return applyPresignedUrlsAsync(articles);
    }

    /**
     * Presigned URL을 비동기로 생성합니다.
     *
     * @param keysToGenerate 생성할 키 목록
     * @return Presigned URL 맵을 포함하는 CompletableFuture
     */
    private CompletableFuture<Map<String, String>> generatePresignedUrlsAsync(
        Set<String> keysToGenerate) {
        return CompletableFuture.supplyAsync(
            () -> s3Service.generatePresignedUrls(keysToGenerate, URL_EXPIRATION_SECONDS));
    }

    /**
     * Presigned URL을 적용하여 게시글 목록을 반환합니다.
     *
     * @param articles 게시글 목록
     * @return Presigned URL이 적용된 게시글 목록
     */
    private List<Article> applyPresignedUrlsAsync(List<Article> articles) {
        Set<String> keysToGenerate = articles.stream()
            .map(Article::getImage)
            .filter(image -> image != null && image.startsWith(S3_BASE_URL))
            .map(image -> image.substring(S3_BASE_URL.length()))
            .collect(Collectors.toSet());

        if (keysToGenerate.isEmpty()) {
            return articles;
        }

        try {
            Map<String, String> presignedUrls = generatePresignedUrlsAsync(keysToGenerate).get();
            return articles.stream()
                .peek(
                    article -> article.setImage(getPresignedUrl(article.getImage(), presignedUrls)))
                .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Presigned URL 생성 중 오류 발생", e);
        }
    }

    /**
     * 주어진 URL에 대해 Presigned URL을 반환합니다.
     *
     * @param url           원본 URL
     * @param presignedUrls Presigned URL 맵
     * @return Presigned URL 또는 원본 URL
     */
    private String getPresignedUrl(String url, Map<String, String> presignedUrls) {
        if (url != null && url.startsWith(S3_BASE_URL)) {
            String key = url.substring(S3_BASE_URL.length());
            return presignedUrls.getOrDefault(key, url);
        }
        return url;
    }
}
