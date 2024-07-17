package com.yoga.backend.article;

import com.yoga.backend.common.entity.Article;

import java.util.List;

public interface ArticleService {
    void saveArticle(Article article);
    List<Article> getAllArticles();
}
