package com.yoga.backend.common.entity.RecordedLectures;


import jakarta.persistence.*;

@Entity
public class RecordedLectureChapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String thumbnail;
    private String videoUrl;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private RecordedLecture lecture;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public RecordedLecture getLecture() {
        return lecture;
    }

    public void setLecture(RecordedLecture lecture) {
        this.lecture = lecture;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
