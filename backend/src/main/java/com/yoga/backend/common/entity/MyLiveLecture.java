package com.yoga.backend.common.entity;

import com.yoga.backend.common.converter.InstantToSqlDateConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * 나의 실시간 강의 엔티티 클래스 특정 사용자가 등록한 실시간 강의
 *
 */

@Setter
@Getter
@Entity
public class MyLiveLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myListId; // 나의 강의 목록 ID (Primary Key)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "live_id", referencedColumnName = "liveId", nullable = false)
    private LiveLectures liveLecture; // 실시간 강의 ID (Foreign Key)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user; // 사용자 ID (Foreign Key)

    @Column
    @Convert(converter = InstantToSqlDateConverter.class)
    private Instant startDate; // 예약 시작 날짜

    @Column
    @Convert(converter = InstantToSqlDateConverter.class)
    private Instant endDate; // 예약 종료 날짜

    public Long getLiveId() {
        return liveLecture.getLiveId();
    }

}
