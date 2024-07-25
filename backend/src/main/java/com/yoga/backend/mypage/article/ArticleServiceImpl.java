package com.yoga.backend.mypage.article;

import com.yoga.backend.common.entity.Article;
import com.yoga.backend.common.awsS3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.net.URL;
import java.net.MalformedURLException;
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
    private static final long URL_EXPIRATION_SECONDS = 86400; // 24시간

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
     * 모든 게시글을 조회합니다.
     *
     * @return 게시글 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<Article> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        return applyPresignedUrlsAsync(articles);
    }

    /**
     * 특정 사용자의 모든 게시글을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 게시글 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<Article> getArticlesByUserId(int userId) {
        List<Article> articles = articleRepository.findByUserId(userId);
        return applyPresignedUrlsAsync(articles);
    }

    /**
     * 특정 게시글을 ID로 조회합니다.
     *
     * @param id 게시글 ID
     * @return 게시글 Optional 객체
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Article> getArticleById(Long id) {
        Optional<Article> articleOpt = articleRepository.findById(id);
        return articleOpt.map(this::applyPresignedUrl);
    }

    /**
     * 게시글을 업데이트합니다.
     *
     * @param articleId  게시글 ID
     * @param newContent 새로운 내용
     * @param newImage   새로운 이미지 URL
     * @return 업데이트된 게시글 객체
     */
    @Override
    @Transactional
    public Article updateArticle(Long articleId, String newContent, String newImage) {
        try {
            Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. ID: " + articleId));

            article.setContent(newContent);

            // 새 이미지가 제공되었고, 기존 이미지와 다른 경우에만 이미지 업데이트
            if (newImage.isBlank()) {
                article.setImageUrl("");
            } else if (!extractKeyFromUrl(newImage).equals(extractKeyFromUrl(article.getImage()))) {
                String oldImage = article.getImage();
                article.setImage(newImage);

                // 기존 이미지가 있고, 새 이미지와 다른 경우에만 기존 이미지 삭제
                if (!oldImage.isBlank() && !extractKeyFromUrl(oldImage).equals(
                    extractKeyFromUrl(newImage))) {
                    String oldKey = extractKeyFromUrl(oldImage);
                    if (oldKey != null) {
                        s3Service.deleteFile(oldKey);
                    }
                }
            }
            article.setUpdatedAt(LocalDateTime.now());
            article = articleRepository.save(article);

            return applyPresignedUrl(article);

        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("게시글 업데이트 중 충돌이 발생했습니다. 다시 시도해 주세요.", e);
        } catch (Exception e) {
            throw new RuntimeException("게시글 업데이트 중 오류가 발생했습니다: " + e.getMessage(), e);
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
                if (article.getImage() != null) {
                    String key = extractKeyFromUrl(article.getImage());
                    if (key != null) {
                        s3Service.deleteFile(key);
                    }
                }
                articleRepository.deleteById(id);
            } else {
                throw new RuntimeException("게시글을 찾을 수 없습니다.");
            }
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("게시글 삭제 중 충돌이 발생했습니다. 다시 시도해 주세요.", e);
        }
    }

    /**
     * 특정 내용이 포함된 게시글을 조회합니다.
     *
     * @param content 검색할 내용
     * @return 게시글 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<Article> findByContent(String content) {
        List<Article> articles = articleRepository.findByContent(content);
        return applyPresignedUrlsAsync(articles);
    }

    /**
     * 비동기로 Presigned URL을 적용합니다.
     *
     * @param articles 게시글 리스트
     * @return Presigned URL이 적용된 게시글 리스트
     */
    private List<Article> applyPresignedUrlsAsync(List<Article> articles) {
        Set<String> keysToGenerate = articles.stream()
            .map(Article::getImage)
            .filter(Objects::nonNull)
            .map(this::extractKeyFromUrl)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        if (keysToGenerate.isEmpty()) {
            return articles;
        }

        try {
            Map<String, String> presignedUrls = generatePresignedUrlsAsync(keysToGenerate).get();
            return articles.stream()
                .map(article -> {
                    String imageUrl = article.getImage();
                    if (imageUrl != null) {
                        String key = extractKeyFromUrl(imageUrl);
                        if (key != null) {
                            String presignedUrl = presignedUrls.getOrDefault(key, imageUrl);
                            article.setImage(normalizeUrl(presignedUrl));
                        }
                    }
                    return article;
                })
                .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Presigned URL 생성 중 오류 발생", e);
        }
    }

    /**
     * 게시글에 Presigned URL을 적용합니다.
     *
     * @param article 게시글 객체
     * @return Presigned URL이 적용된 게시글 객체
     */
    private Article applyPresignedUrl(Article article) {
        String imageUrl = article.getImage();

        if (imageUrl != null) {
            String key = extractKeyFromUrl(imageUrl);
            if (key != null) {
                String presignedUrl = s3Service.generatePresignedUrl(key, URL_EXPIRATION_SECONDS);
                article.setImage(normalizeUrl(presignedUrl));
            }
        }
        return article;
    }

    /**
     * URL에서 S3 키를 추출합니다.
     *
     * @param url URL 문자열
     * @return S3 키 문자열
     */
    private String extractKeyFromUrl(String url) {
        if (url.isBlank()) {
            return null;
        }
        if (url.startsWith(S3_BASE_URL)) {
            String key = url.substring(S3_BASE_URL.length());
            int queryIndex = key.indexOf('?');
            return queryIndex > 0 ? key.substring(0, queryIndex) : key;
        }
        return null;
    }

    /**
     * URL을 정규화합니다.
     *
     * @param url URL 문자열
     * @return 정규화된 URL 문자열
     */
    private String normalizeUrl(String url) {
        try {
            URL parsedUrl = new URL(url);
            String baseUrl =
                parsedUrl.getProtocol() + "://" + parsedUrl.getHost() + parsedUrl.getPath();
            String query = parsedUrl.getQuery();

            if (query == null) {
                return baseUrl;
            }

            Map<String, String> params = new LinkedHashMap<>();
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=", 2);
                if (keyValue.length == 2) {
                    params.putIfAbsent(keyValue[0], keyValue[1]);
                }
            }

            StringBuilder normalizedQuery = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (normalizedQuery.length() > 0) {
                    normalizedQuery.append('&');
                }
                normalizedQuery.append(entry.getKey()).append('=').append(entry.getValue());
            }

            return baseUrl + "?" + normalizedQuery;
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL 정규화 실패", e);
        }
    }

    /**
     * 비동기로 Presigned URL을 생성합니다.
     *
     * @param keysToGenerate 생성할 키 셋
     * @return Presigned URL 맵
     */
    private CompletableFuture<Map<String, String>> generatePresignedUrlsAsync(
        Set<String> keysToGenerate) {
        return CompletableFuture.supplyAsync(
            () -> s3Service.generatePresignedUrls(keysToGenerate, URL_EXPIRATION_SECONDS));
    }
}
