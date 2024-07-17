package com.yoga.backend.article;

import com.yoga.backend.common.entity.Article;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/mypage/notification")
public class ArticleController {

    private final ArticleService articleService;
    private final UsersRepository usersRepository;

    @Autowired
    public ArticleController(ArticleService articleService, UsersRepository usersRepository) {
        this.articleService = articleService;
        this.usersRepository = usersRepository;
    }

    @PostMapping("/write")
    public ResponseEntity<String> createArticle(@RequestBody ArticleDto articleDto) {
        try {
            // Get the currently authenticated user
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username;
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }

            Optional<Users> optionalUser = usersRepository.findByEmail(username).stream().findFirst();
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            Users user = optionalUser.get();
            Article article = new Article();
            article.setUser(user);
            article.setTitle(articleDto.getTitle());
            article.setContent(articleDto.getContent());
            article.setImageUrl(articleDto.getImageUrl());

            articleService.saveArticle(article);
            return ResponseEntity.status(HttpStatus.CREATED).body("Article created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Article creation failed");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Article>> getAllArticles() {
        try {
            List<Article> articles = articleService.getAllArticles();
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
