package com.yoga.backend.members.dto;

import java.util.List;

public class UpdateDto {
    private String password;
    private String nickname;
    private boolean teacher;
    private int authnumber;
    private String imageUrl;
    private List<String> hashTags;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isTeacher() {
        return teacher;
    }

    public void setTeacher(boolean teacher) {
        this.teacher = teacher;
    }

    public int getAuthnumber() {
        return authnumber;
    }

    public void setAuthnumber(int authnumber) {
        this.authnumber = authnumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getHashTags() {
        return hashTags;
    }

    public void setHashTags(List<String> hashTags) {
        this.hashTags = hashTags;
    }
}
