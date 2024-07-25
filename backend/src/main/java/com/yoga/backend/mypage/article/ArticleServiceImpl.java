package com.yoga.backend.mypage.article;

import com.yoga.backend.common.entity.Article;
import com.yoga.backend.common.awsS3.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class ArticleServiceImpl implements ArticleService {

    private static final String S3_BASE_URL = "https://yoga-navi.s3.ap-northeast-2.amazonaws.com/";
    private static final long URL_EXPIRATION_SECONDS = 86400; // 24 hours
    private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final ArticleRepository articleRepository;
    private final S3Service s3Service;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, S3Service s3Service) {
        this.articleRepository = articleRepository;
        this.s3Service = s3Service;
    }

    @Override
    @Transactional
    public void saveArticle(Article article) {
        try {
            articleRepository.save(article);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("게시글 저장 중 충돌이 발생했습니다. 다시 시도해 주세요.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Article> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        return applyPresignedUrlsAsync(articles);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Article> getArticlesByUserId(int userId) {
        List<Article> articles = articleRepository.findByUserId(userId);
        return applyPresignedUrlsAsync(articles);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Article> getArticleById(Long id) {
        Optional<Article> articleOpt = articleRepository.findById(id);
        return articleOpt.map(this::applyPresignedUrl);
    }

    @Override
    @Transactional
    public Article updateArticle(Long articleId, String newContent, String newImage) {
        try {
            Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. ID: " + articleId));

            article.setContent(newContent);

            // 새 이미지가 제공되었고, 기존 이미지와 다른 경우에만 이미지 업데이트
            log.info("newImage: {}", extractKeyFromUrl(article.getImage()));
            log.info("newImage: {}", article);
            log.info("newImage: {}", newImage);
            if (newImage.isBlank()) {
                log.info("1111");
                article.setImageUrl("");
            } else if (!extractKeyFromUrl(newImage).equals(extractKeyFromUrl(article.getImage()))) {
                log.info("2222");
                String oldImage = article.getImage();
                article.setImage(newImage);

                log.info("newImage: {}", article);
                // 기존 이미지가 있고, 새 이미지와 다른 경우에만 기존 이미지 삭제
                log.info("oldImage: {}", oldImage);
                log.info("extractKeyFromUrl: {}", extractKeyFromUrl(oldImage));

                if (!oldImage.isBlank() && !extractKeyFromUrl(oldImage).equals(extractKeyFromUrl(newImage))) {
                    log.info("2.1");
                    String oldKey = extractKeyFromUrl(oldImage);
                    if (oldKey != null) {
                        log.info("2.2");
                        s3Service.deleteFile(oldKey);
                    }
                }
            }
            log.info("3333");
            log.info("articleddd: {}", article);
            article.setUpdatedAt(LocalDateTime.now());
            article = articleRepository.save(article);
            log.info("4444");
            log.info("articleddd: {}", article);

            return applyPresignedUrl(article);

        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("게시글 업데이트 중 충돌이 발생했습니다. 다시 시도해 주세요.", e);
        } catch (Exception e) {
            throw new RuntimeException("게시글 업데이트 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

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

    @Override
    @Transactional(readOnly = true)
    public List<Article> findByContent(String content) {
        List<Article> articles = articleRepository.findByContent(content);
        return applyPresignedUrlsAsync(articles);
    }

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

    private Article applyPresignedUrl(Article article) {
        String imageUrl = article.getImage();
        log.info("imageUrl: {}", imageUrl);

        if (imageUrl != null) {
            String key = extractKeyFromUrl(imageUrl);
            log.info("imageUrl: {}", key);
            if (key != null) {
                String presignedUrl = s3Service.generatePresignedUrl(key, URL_EXPIRATION_SECONDS);
                article.setImage(normalizeUrl(presignedUrl));
            }
        }
        return article;
    }

    private String extractKeyFromUrl(String url) {
        if (url.isBlank()) return null;
        log.info("extractKeyFromUrl: 1");
        if (url.startsWith(S3_BASE_URL)) {
            log.info("extractKeyFromUrl: 2");

            String key = url.substring(S3_BASE_URL.length());
            int queryIndex = key.indexOf('?');
            return queryIndex > 0 ? key.substring(0, queryIndex) : key;
        }
        log.info("extractKeyFromUrl: 3");
        return null;
    }

    private String normalizeUrl(String url) {
        try {
            URL parsedUrl = new URL(url);
            String baseUrl = parsedUrl.getProtocol() + "://" + parsedUrl.getHost() + parsedUrl.getPath();
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

    private CompletableFuture<Map<String, String>> generatePresignedUrlsAsync(Set<String> keysToGenerate) {
        return CompletableFuture.supplyAsync(() -> s3Service.generatePresignedUrls(keysToGenerate, URL_EXPIRATION_SECONDS));
    }
}