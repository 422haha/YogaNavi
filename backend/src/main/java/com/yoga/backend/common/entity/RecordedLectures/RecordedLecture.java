package com.yoga.backend.common.entity.RecordedLectures;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class RecordedLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String title;

    @Column(name = "record_content", nullable = false)
    private String content;

    @Column(nullable = false)
    private String thumbnail;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL)
    private List<RecordedLectureChapter> chapters;

    @Column(nullable = false)
    private String creationStatus;

    @Column(nullable = false)
    private long likeCount = 0;

    @Column(nullable = false)
    private Date dateCreated;

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public List<RecordedLectureChapter> getChapters() {
        return chapters;
    }

    public void setChapters(
        List<RecordedLectureChapter> chapters) {
        this.chapters = chapters;
    }

    public String getCreationStatus() {
        return creationStatus;
    }

    public void setCreationStatus(String creationStatus) {
        this.creationStatus = creationStatus;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}