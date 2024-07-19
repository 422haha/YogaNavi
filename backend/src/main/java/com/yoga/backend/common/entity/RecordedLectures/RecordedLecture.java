package com.yoga.backend.common.entity.RecordedLectures;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class RecordedLecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String title; // Changed from record_title

    @Column(name = "record_content", nullable = false)
    private String content; // Changed from record_content

    @Column(nullable = false)
    private String thumbnail;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL)
    private List<RecordedLectureChapter> chapters;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}