package com.yoga.backend.mypage.article;

import lombok.Data;

/**
 * 게시글(공지사항) DTO 클래스
 */
@Data
public class ArticleDto {

    private String content; // 게시글 내용
    private String imageUrl; // 이미지 URL
    private String imageUrlSmall;

}
