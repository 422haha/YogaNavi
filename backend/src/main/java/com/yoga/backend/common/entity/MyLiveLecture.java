package com.yoga.backend.common.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources.Chain.Strategy;

/**
 * 나의 실시간 강의 엔티티 클래스
 * 특정 사용자가 등록한 실시간 강의
 */

@Setter
@Getter
@Entity
public class MyLiveLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myListId; // 나의 강의 목록 ID (Primary Key)

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)//채티타입 레이지
    @JoinColumn(name = "live_id", referencedColumnName = "liveId", nullable = false)
    private LiveLectures liveLecture; // 실시간 강의 ID (Foreign Key)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user; // 사용자 ID (Foreign Key)

    public Long getLiveId() {
        return liveLecture.getLiveId();
    }
}