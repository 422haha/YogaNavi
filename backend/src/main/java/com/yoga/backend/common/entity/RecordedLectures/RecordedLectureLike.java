package com.yoga.backend.common.entity.RecordedLectures;


import com.yoga.backend.common.entity.Users;
import jakarta.persistence.*;

@Entity
@Table(name = "RecordedLectureLike")
public class RecordedLectureLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private RecordedLecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

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

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
