package com.yoga.backend.article;

import lombok.Data;

@Data
public class ArticleDto {
    private String title;
    private String content;
    private String imageUrl;
}
