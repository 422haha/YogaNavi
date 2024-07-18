package com.yoga.backend.common.entity.RecordedLectures;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "RecordedLecture")
public class RecordedLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String thumbnailUrl;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("chapterOrder ASC")
    private List<RecordedLectureChapter> chapters = new ArrayList<>();

    // Getters and Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public List<RecordedLectureChapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<RecordedLectureChapter> chapters) {
        this.chapters = chapters;
    }

    // Helper method to add a chapter
    public void addChapter(RecordedLectureChapter chapter) {
        chapters.add(chapter);
        chapter.setLecture(this);
    }

    // Helper method to remove a chapter
    public void removeChapter(RecordedLectureChapter chapter) {
        chapters.remove(chapter);
        chapter.setLecture(null);
    }
}
