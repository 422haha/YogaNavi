package com.yoga.backend.livelectures.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.entity.QLiveLectures;
import com.yoga.backend.common.entity.QMyLiveLecture;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


public class HomeRepository {
    private final JPAQueryFactory queryFactory;

    public HomeRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Page<LiveLectures> findLecturesByUserAndDateRange(int userId, Instant currentDate, String dayOfWeek, Pageable pageable) {
        QLiveLectures liveLectures = QLiveLectures.liveLectures;

        List<LiveLectures> content = queryFactory
            .selectFrom(liveLectures)
            .where(liveLectures.user.id.eq(userId)
                .and(liveLectures.startDate.loe(currentDate).and(liveLectures.endDate.goe(currentDate))
                    .or(liveLectures.endDate.gt(currentDate)))
                .and(liveLectures.availableDay.like("%" + dayOfWeek + "%")))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(liveLectures)
            .where(liveLectures.user.id.eq(userId)
                .and(liveLectures.startDate.loe(currentDate).and(liveLectures.endDate.goe(currentDate))
                    .or(liveLectures.endDate.gt(currentDate)))
                .and(liveLectures.availableDay.like("%" + dayOfWeek + "%")))
            .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    public Page<MyLiveLecture> findCurrentMyLectures(int userId, Instant currentDate, String dayOfWeek, Pageable pageable) {
        QMyLiveLecture myLiveLecture = QMyLiveLecture.myLiveLecture;
        QLiveLectures liveLectures = QLiveLectures.liveLectures;

        List<MyLiveLecture> content = queryFactory
            .selectFrom(myLiveLecture)
            .join(myLiveLecture.liveLecture, liveLectures).fetchJoin()
            .where(myLiveLecture.user.id.eq(userId)
                .and(myLiveLecture.startDate.loe(currentDate).and(myLiveLecture.endDate.goe(currentDate))
                    .or(myLiveLecture.endDate.gt(currentDate)))
                .and(liveLectures.availableDay.like("%" + dayOfWeek + "%")))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(myLiveLecture)
            .join(myLiveLecture.liveLecture, liveLectures)
            .where(myLiveLecture.user.id.eq(userId)
                .and(myLiveLecture.startDate.loe(currentDate).and(myLiveLecture.endDate.goe(currentDate))
                    .or(myLiveLecture.endDate.gt(currentDate)))
                .and(liveLectures.availableDay.like("%" + dayOfWeek + "%")))
            .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

}
