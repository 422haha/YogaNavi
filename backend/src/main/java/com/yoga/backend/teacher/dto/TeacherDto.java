package com.yoga.backend.teacher.dto;

import java.util.Set;

/**
 * 강사 DTO 클래스
 */
public class TeacherDto {

    private int id;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String content;
    private Set<String> hashtags;
    private boolean liked;
    private int likeCount;

    /**
     * TeacherDto 생성자
     *
     * @param id               강사 ID
     * @param email            강사 이메일
     * @param nickname         강사 닉네임
     * @param profileImageUrl  강사 프로필 이미지 URL
     * @param content          강사 소개
     * @param hashtags         강사 해시태그
     * @param liked            좋아요 여부
     * @param likeCount        좋아요 수
     */
    public TeacherDto(int id, String email, String nickname, String profileImageUrl, String content, Set<String> hashtags, boolean liked, int likeCount) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.content = content;
        this.hashtags = hashtags;
        this.liked = liked;
        this.likeCount = likeCount;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(Set<String> hashtags) {
        this.hashtags = hashtags;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
