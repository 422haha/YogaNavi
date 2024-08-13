package com.yoga.backend.article;

import com.yoga.backend.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 게시글(공지사항) 관련 요청을 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/mypage/notification")
public class ArticleController {

    private final ArticleService articleService;
    private final JwtUtil jwtUtil;

    public ArticleController(ArticleService articleService,
        JwtUtil jwtUtil) {
        this.articleService = articleService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 새로운 게시글 작성
     *
     * @param token      JWT 토큰
     * @param articleDto 게시글 DTO
     * @return 작성 결과 메시지, 데이터
     */
    @PostMapping("/write")
    public ResponseEntity<Map<String, Object>> saveArticle(
        @RequestHeader("Authorization") String token, @RequestBody ArticleDto articleDto) {
        log.info("게시글 저장 시도");
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = jwtUtil.getUserIdFromToken(token);
            ArticleDto saveArticle = articleService.saveArticle(userId, articleDto);
            log.info("게시글 저장 성공: 사용자 ID {}", userId);
            response.put("message", "게시글 작성 성공");
            response.put("data", saveArticle);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("게시글 저장 중 오류 발생: {}", e.getMessage());
            response.put("message", "서버 내부 오류가 발생했습니다");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 특정 사용자가 작성한 모든 게시글 조회
     *
     * @param token JWT 토큰
     * @return 게시글 목록
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getArticlesByUserId(
        @RequestHeader("Authorization") String token) {
        log.info("사용자 게시글 목록 조회 시도");
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = jwtUtil.getUserIdFromToken(token);
            List<ArticleDto> articleDtos = articleService.getArticlesByUserId(userId);
            log.info("게시글 목록 조회 성공: 사용자 ID {}, 게시글 수 {}", userId, articleDtos.size());
            response.put("message", "success");
            response.put("data", articleDtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("게시글 목록 조회 중 오류 발생: {}", e.getMessage());
            response.put("message", "서버 내부 오류가 발생했습니다");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 특정 게시글 조회
     *
     * @param id 게시글 ID
     * @return 게시글 정보
     */
    @GetMapping("/update/{article_id}")
    public ResponseEntity<Map<String, Object>> getArticleById(
        @PathVariable("article_id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<ArticleDto> article = articleService.getArticleById(id);
            if (article.isPresent()) {
                response.put("message", "success");
                response.put("data", article.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "게시글을 찾을 수 없습니다");
                response.put("data", new Object[]{});
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("message", "서버 내부 오류가 발생했습니다");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 특정 게시글 업데이트
     *
     * @param token      JWT 토큰
     * @param id         게시글 ID
     * @param articleDto 수정할 게시글 정보
     * @return 수정 결과 메시지와 데이터
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateArticle(
        @RequestHeader("Authorization") String token, @PathVariable Long id,
        @RequestBody ArticleDto articleDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = jwtUtil.getUserIdFromToken(token);
            ArticleDto updatedArticle = articleService.updateArticle(userId, id, articleDto);
            response.put("message", "게시글 수정 성공");
            response.put("data", updatedArticle);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("message", "서버 내부 오류가 발생했습니다");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 특정 게시글 삭제
     *
     * @param token JWT 토큰
     * @param id    게시글 ID
     * @return 삭제 결과 메시지, 데이터
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteArticle(
        @RequestHeader("Authorization") String token, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = jwtUtil.getUserIdFromToken(token);
            boolean isDeleted = articleService.deleteArticle(userId, id);
            if (isDeleted) {
                response.put("message", "게시글 삭제 성공");
                response.put("data", new Object[]{});
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("message", "게시글을 찾을 수 없습니다");
                response.put("data", new Object[]{});
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("message", "서버 내부 오류가 발생했습니다");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
