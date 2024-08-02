package com.yoga.backend.teacher.dto;

import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 강사 DTO 클래스
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
public class TeacherDto {

    private int id;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String profileImageUrlSmall;
    private String content;
    private Set<String> hashtags;
    private boolean liked;
    private int likeCount;

    /**
     * TeacherDto 생성자
     *
     * @param id                   강사 ID
     * @param email                강사 이메일
     * @param nickname             강사 닉네임
     * @param profileImageUrl      강사 프로필 이미지 URL
     * @param profileImageUrlSmall 강사 프로필 이미지 작은 URL
     * @param content              강사 소개
     * @param hashtags             강사 해시태그
     * @param liked                좋아요 여부
     * @param likeCount            좋아요 수
     */
    public TeacherDto(int id, String email, String nickname, String profileImageUrl,
        String profileImageUrlSmall, String content, Set<String> hashtags, boolean liked,
        int likeCount) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.profileImageUrlSmall = profileImageUrlSmall;
        this.content = content;
        this.hashtags = hashtags;
        this.liked = liked;
        this.likeCount = likeCount;
    }
}
