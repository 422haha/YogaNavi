package com.yoga.backend.article;

import com.yoga.backend.common.entity.Article;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.common.service.S3Service;
import com.yoga.backend.members.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 게시글(공지사항) 서비스 구현 클래스
 */
@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final UsersRepository usersRepository;
    private final S3Service s3Service;

    /**
     * ArticleServiceImpl 생성자
     *
     * @param articleRepository ArticleRepository 객체
     * @param usersRepository   UsersRepository 객체
     * @param s3Service         S3Service 객체
     */
    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, UsersRepository usersRepository,
        S3Service s3Service) {
        this.articleRepository = articleRepository;
        this.usersRepository = usersRepository;
        this.s3Service = s3Service;
    }

    /**
     * 게시글을 저장
     *
     * @param userId     사용자 ID
     * @param articleDto 저장할 게시글 DTO
     * @return 생성된 ArticleDto 객체
     */
    @Override
    @Transactional
    public ArticleDto saveArticle(int userId, ArticleDto articleDto) {
        log.info("게시글 저장 시작: 사용자 ID {}", userId);
        Optional<Users> optionalUser = usersRepository.findById(userId);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            Article article = new Article();
            article.setUser(user);
            article.setContent(articleDto.getContent());
            article.setImage(articleDto.getImageUrl());
            article.setImageUrlSmall(articleDto.getImageUrlSmall());
            article.setCreatedAt(LocalDateTime.now());
            article.setUpdatedAt(LocalDateTime.now());

            Article savedArticle = articleRepository.save(article);
            log.info("게시글 저장 완료: 게시글 ID {}", savedArticle.getArticleId());
            return convertArticleToDto(savedArticle);
        }
        log.error("게시글 저장 실패: 유효하지 않은 사용자 ID {}", userId);
        throw new RuntimeException("유효하지 않은 사용자 ID입니다.");
    }

    /**
     * 특정 사용자의 모든 게시글을 조회
     *
     * @param userId 사용자 ID
     * @return 게시글 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<ArticleDto> getArticlesByUserId(int userId) {
        List<Article> articles = articleRepository.findByUserIdWithUser(userId);
        return articles.stream()
            .sorted((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()))
            .map(this::convertArticleToDto)
            .collect(Collectors.toList());
    }

    /**
     * 특정 게시글을 ID로 조회
     *
     * @param id 게시글 ID
     * @return 게시글 DTO Optional 객체
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Optional<ArticleDto> getArticleById(Long id) {
        return articleRepository.findByIdWithUser(id)
            .map(this::convertArticleToDto);
    }

    /**
     * 게시글을 업데이트
     *
     * @param userId     사용자 ID
     * @param articleId  게시글 ID
     * @param articleDto 업데이트할 게시글 정보
     * @return 업데이트된 게시글 DTO 객체
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ArticleDto updateArticle(int userId, Long articleId, ArticleDto articleDto) {
        log.info("게시글 수정 시작: 게시글 ID {}, 사용자 ID {}", articleId, userId);
        try {
            Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> {
                    log.error("게시글을 찾을 수 없음: 게시글 ID {}", articleId);
                    return new RuntimeException("게시글을 찾을 수 없습니다. ID: " + articleId);
                });

            if (article.getUser().getId() != userId) {
                log.error("게시글 수정 권한 없음: 게시글 ID {}, 시도한 사용자 ID {}", articleId, userId);
                throw new RuntimeException("권한이 없습니다.");
            }

            // 이전 이미지 키 저장
            String oldImage = article.getImage();
            String oldImageSmall = article.getImageUrlSmall();

            // 새 이미지 설정
            article.setContent(articleDto.getContent());
            article.setImage(articleDto.getImageUrl());
            article.setImageUrlSmall(articleDto.getImageUrlSmall());
            article.setUpdatedAt(LocalDateTime.now());

            // 이전 이미지 삭제
            if (oldImage != null && !oldImage.isEmpty() && !oldImage.equals(
                articleDto.getImageUrl())) {
                s3Service.deleteFile(oldImage);
            }
            if (oldImageSmall != null && !oldImageSmall.isEmpty() && !oldImageSmall.equals(
                articleDto.getImageUrlSmall())) {
                s3Service.deleteFile(oldImageSmall);
            }

            Article updatedArticle = articleRepository.save(article);
            log.info("게시글 수정 완료: 게시글 ID {}", articleId);
            return convertArticleToDto(updatedArticle);
        } catch (OptimisticLockingFailureException e) {
            log.error("게시글 수정 중 충돌 발생: 게시글 ID {}", articleId);
            throw new RuntimeException("게시글 업데이트 중 충돌이 발생했습니다. 다시 시도해 주세요.", e);
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생: 게시글 ID {}, 오류 메시지 {}", articleId, e.getMessage());
            throw new RuntimeException("게시글 업데이트 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 게시글을 삭제
     *
     * @param userId 사용자 ID
     * @param id     삭제할 게시글 ID
     * @return 삭제 성공 여부
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean deleteArticle(int userId, Long id) {
        try {
            Optional<Article> articleOpt = articleRepository.findById(id);
            if (articleOpt.isPresent()) {
                Article article = articleOpt.get();
                if (article.getUser().getId() != userId) {
                    throw new RuntimeException("권한이 없습니다.");
                }
                s3Service.deleteFile(article.getImage());
                s3Service.deleteFile(article.getImageUrlSmall());
                articleRepository.deleteById(id);
                return true;
            } else {
                return false;
            }
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("게시글 삭제 중 충돌이 발생했습니다. 다시 시도해 주세요.", e);
        }
    }

    /**
     * 특정 내용이 포함된 게시글을 조회
     *
     * @param content 검색할 내용
     * @return 게시글 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<ArticleDto> findByContent(String content) {
        List<Article> articles = articleRepository.findByContent(content);
        return articles.stream()
            .map(this::convertArticleToDto)
            .collect(Collectors.toList());
    }

    /**
     * Article 엔티티를 ArticleDto로 변환
     *
     * @param article 변환할 Article 엔티티
     * @return 변환된 ArticleDto 객체
     */
    private ArticleDto convertArticleToDto(Article article) {
        ArticleDto dto = new ArticleDto();
        dto.setArticleId(article.getArticleId());
        dto.setContent(article.getContent());
        dto.setImageUrl(article.getImage());
        dto.setImageUrlSmall(article.getImageUrlSmall());
        dto.setCreatedAt(
            article.getCreatedAt().atZone(ZoneOffset.ofHours(9)).toInstant().toEpochMilli());
        dto.setUpdatedAt(
            article.getUpdatedAt().atZone(ZoneOffset.ofHours(9)).toInstant().toEpochMilli());

        Users author = article.getUser();
        if (author != null) {
            dto.setUserName(author.getNickname());
            dto.setProfileImageUrl(author.getProfile_image_url());
            dto.setProfileImageSmallUrl(author.getProfile_image_url_small());
        }

        return dto;
    }
}
