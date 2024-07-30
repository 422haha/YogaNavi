package com.yoga.backend.common.entity;

import com.yoga.backend.common.entity.RecordedLectures.RecordedLecture;
import com.yoga.backend.common.entity.RecordedLectures.RecordedLectureLike;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Users")
public class Users {// 여러 사용자나 프로세스가 동시에 같은 회원 정보를 수정하려고 할 때 발생할 수 있는 충돌을 방지하기 위함.

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "user_id", unique = true)
    private int id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String pwd;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(length = 512)
    private String profile_image_url;

    @Column(length = 512)
    private String profile_image_url_small;

    @Column(nullable = false)
    private String role;

    @Column
    private String resetToken;

    @Column(length = 100)
    private String content; // 강사 소개 내용

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Article> articles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LiveLectures> liveLectures = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MyLiveLecture> myLiveLectures = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_hashtags",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    private Set<Hashtag> hashtags = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private List<RecordedLecture> recordedLectures = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RecordedLectureLike> recordedLectureLikes = new ArrayList<>();

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TeacherLike> teacherLikes = new ArrayList<>();

    @Column
    private Instant deletedAt;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
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

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getProfile_image_url_small() {
        return profile_image_url_small;
    }

    public void setProfile_image_url_small(String profile_image_url_small) {
        this.profile_image_url_small = profile_image_url_small;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<Hashtag> getHashtags() {
        return hashtags;
    }

    public void setHashtags(Set<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }

    public void addHashtag(Hashtag hashtag) {
        if (this.hashtags == null) {
            this.hashtags = new HashSet<>();
        }
        this.hashtags.add(hashtag);
        if (hashtag.getUsers() == null) {
            hashtag.setUsers(new HashSet<>());
        }
        hashtag.getUsers().add(this);
    }

    public void removeHashtag(Hashtag hashtag) {
        this.hashtags.remove(hashtag);
        hashtag.getUsers().remove(this);
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public void addArticle(Article article) {
        articles.add(article);
        article.setUser(this);
    }

    public void removeAllArticles() {
        for (Article article : new ArrayList<>(articles)) {
            removeArticle(article);
        }
    }

    public void removeArticle(Article article) {
        articles.remove(article);
        article.setUser(null);
    }

    public List<LiveLectures> getLiveLectures() {
        return liveLectures;
    }

    public void setLiveLectures(List<LiveLectures> liveLectures) {
        this.liveLectures = liveLectures;
    }

    public List<MyLiveLecture> getMyLiveLectures() {
        return myLiveLectures;
    }

    public void setMyLiveLectures(List<MyLiveLecture> myLiveLectures) {
        this.myLiveLectures = myLiveLectures;
    }

    public List<RecordedLecture> getRecordedLectures() {
        return recordedLectures;
    }

    public void setRecordedLectures(List<RecordedLecture> recordedLectures) {
        this.recordedLectures = recordedLectures;
    }

    public List<RecordedLectureLike> getRecordedLectureLikes() {
        return recordedLectureLikes;
    }

    public void setRecordedLectureLikes(List<RecordedLectureLike> recordedLectureLikes) {
        this.recordedLectureLikes = recordedLectureLikes;
    }

    public List<TeacherLike> getTeacherLikes() {
        return teacherLikes;
    }

    public void setTeacherLikes(List<TeacherLike> teacherLikes) {
        this.teacherLikes = teacherLikes;
    }

    public Instant  getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

}
