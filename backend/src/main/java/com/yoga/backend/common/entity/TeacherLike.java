package com.yoga.backend.common.entity;

import jakarta.persistence.*;

/**
 * 강사 좋아요 엔티티 클래스
 */
@Entity
@Table(name = "TeacherLike")
public class TeacherLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Users teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getTeacher() {
        return teacher;
    }

    public void setTeacher(Users teacher) {
        this.teacher = teacher;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
