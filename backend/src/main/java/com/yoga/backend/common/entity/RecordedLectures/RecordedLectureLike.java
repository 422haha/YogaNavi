package com.yoga.backend.common.entity.RecordedLectures;


import jakarta.persistence.*;

@Entity
@Table(name = "RecordedLectureLike")
public class RecordedLectureLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private RecordedLecture lecture;

    @Column
    private String userEmail;

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RecordedLecture getLecture() {
        return lecture;
    }

    public void setLecture(RecordedLecture lecture) {
        this.lecture = lecture;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
