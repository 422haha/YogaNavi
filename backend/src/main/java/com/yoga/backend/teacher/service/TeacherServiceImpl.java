package com.yoga.backend.teacher.service;

import com.yoga.backend.common.awsS3.S3Service;
import com.yoga.backend.common.entity.Hashtag;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.common.entity.TeacherLike;
import com.yoga.backend.mypage.recorded.repository.RecordedLectureLikeRepository;
import com.yoga.backend.teacher.TeacherFilter;
import com.yoga.backend.teacher.dto.DetailedTeacherDto;
import com.yoga.backend.teacher.dto.TeacherDto;
import com.yoga.backend.teacher.repository.TeacherLikeRepository;
import com.yoga.backend.teacher.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 강사 서비스 구현 클래스
 */
@Service
public class TeacherServiceImpl implements TeacherService {

    private static final long URL_EXPIRATION_SECONDS = 86400; // 24시간

    private final TeacherRepository teacherRepository;
    private final TeacherLikeRepository teacherLikeRepository;
    private final RecordedLectureLikeRepository recordedLectureLikeRepository;
    private final S3Service s3Service;

    @Autowired
    public TeacherServiceImpl(TeacherRepository teacherRepository,
        TeacherLikeRepository teacherLikeRepository,
        RecordedLectureLikeRepository recordedLectureLikeRepository,
        S3Service s3Service) {
        this.teacherRepository = teacherRepository;
        this.teacherLikeRepository = teacherLikeRepository;
        this.recordedLectureLikeRepository = recordedLectureLikeRepository;
        this.s3Service = s3Service;
    }

    /**
     * 모든 강사 정보를 조회합니다.
     *
     * @param filter  필터 조건
     * @param sorting 정렬 기준 (0: 최신순, 1: 인기순)
     * @param userId  사용자 ID
     * @return 강사 정보 리스트
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<TeacherDto> getAllTeachers(TeacherFilter filter, int sorting, int userId) {
        LocalDate now = LocalDate.now();
        long weekAfter = now.plusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long monthAfter = now.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long threeMonthsAfter = now.plusMonths(3).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        System.out.println("Filter Start Time: " + filter.getStartTime());
        System.out.println("Filter End Time: " + filter.getEndTime());
        System.out.println("Filter Days: " + filter.getDay());
        System.out.println("Filter Period: " + filter.getPeriod());
        System.out.println("Week After: " + weekAfter);
        System.out.println("Month After: " + monthAfter);
        System.out.println("Three Months After: " + threeMonthsAfter);
        System.out.println("Filter Max Live Num: " + filter.getMaxLiveNum());
        List<Users> users = teacherRepository.findTeachersByLectureFilter(
            filter.getStartTime(),
            filter.getEndTime(),
            filter.getDay(),
            filter.getPeriod(),
            weekAfter,
            monthAfter,
            threeMonthsAfter,
            filter.getMaxLiveNum()
        );
        System.out.println("Filtered users: " + users.size());
        return users.stream()
            .filter(user -> {
                if (filter.getSearchKeyword() == null || filter.getSearchKeyword().isEmpty()) {
                    return true;
                }
                return user.getNickname().contains(filter.getSearchKeyword()) ||
                    user.getEmail().contains(filter.getSearchKeyword()) ||
                    user.getHashtags().stream()
                        .anyMatch(hashtag -> hashtag.getName().contains(filter.getSearchKeyword()));
            })
            .map(user -> {
                String profileImageUrl = null;
                String profileImageUrlSmall = null;
                try {
                    if (user.getProfile_image_url() != null && !user.getProfile_image_url()
                        .isEmpty()) {
                        profileImageUrl = s3Service.generatePresignedUrl(
                            user.getProfile_image_url(), URL_EXPIRATION_SECONDS);
                    }
                    if (user.getProfile_image_url_small() != null
                        && !user.getProfile_image_url_small().isEmpty()) {
                        profileImageUrlSmall = s3Service.generatePresignedUrl(
                            user.getProfile_image_url_small(), URL_EXPIRATION_SECONDS);
                    }
                } catch (Exception e) {
                    System.err.println("Presigned URL 생성 오류: " + e.getMessage());
                }

                boolean likedByUser =
                    teacherLikeRepository.findByTeacherAndUser(user, getUserById(userId)) != null;

                return TeacherDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .profileImageUrl(profileImageUrl)
                    .profileImageUrlSmall(profileImageUrlSmall)
                    .content(user.getContent())
                    .hashtags(user.getHashtags().stream().map(Hashtag::getName)
                        .collect(Collectors.toSet()))
                    .liked(likedByUser)
                    .likeCount(user.getTeacherLikes().size())
                    .build();
            })
            .sorted((user1, user2) -> {
                if (sorting == 0) {
                    return Integer.compare(user2.getId(), user1.getId());
                } else {
                    return Integer.compare(user2.getLikeCount(), user1.getLikeCount());
                }
            })
            .collect(Collectors.toList());
    }

    /**
     * 특정 강사 정보를 ID로 조회합니다.
     *
     * @param teacherId 강사 ID
     * @param userId    사용자 ID
     * @return 상세 강사 정보
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public DetailedTeacherDto getTeacherById(int teacherId, int userId) {
        Users user = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("강사를 찾을 수 없습니다."));

        String profileImageUrl = null;
        String profileImageUrlSmall = null;
        try {
            if (user.getProfile_image_url() != null && !user.getProfile_image_url().isEmpty()) {
                profileImageUrl = s3Service.generatePresignedUrl(user.getProfile_image_url(),
                    URL_EXPIRATION_SECONDS);
                System.out.println("생성된 profileImageUrl: " + profileImageUrl);
            }
            if (user.getProfile_image_url_small() != null && !user.getProfile_image_url_small()
                .isEmpty()) {
                profileImageUrlSmall = s3Service.generatePresignedUrl(
                    user.getProfile_image_url_small(), URL_EXPIRATION_SECONDS);
                System.out.println("생성된 profileImageUrlSmall: " + profileImageUrlSmall);
            }
        } catch (Exception e) {
            System.err.println("Presigned URL 생성 오류: " + e.getMessage());
        }

        boolean likedByUser =
            teacherLikeRepository.findByTeacherAndUser(user, getUserById(userId)) != null;

        final String finalProfileImageUrl = profileImageUrl;
        final String finalProfileImageUrlSmall = profileImageUrlSmall;

        List<DetailedTeacherDto.LectureDto> sortedRecordedLectures = user.getRecordedLectures()
            .stream()
            .map(lecture -> {
                String recordThumbnail = null;
                String recordThumbnailSmall = null;
                try {
                    if (lecture.getThumbnail() != null && !lecture.getThumbnail().isEmpty()) {
                        recordThumbnail = s3Service.generatePresignedUrl(lecture.getThumbnail(),
                            URL_EXPIRATION_SECONDS);
                    }
                    if (lecture.getThumbnailSmall() != null && !lecture.getThumbnailSmall()
                        .isEmpty()) {
                        recordThumbnailSmall = s3Service.generatePresignedUrl(
                            lecture.getThumbnailSmall(), URL_EXPIRATION_SECONDS);
                    }
                } catch (Exception e) {
                    System.err.println("Recorded Lecture Presigned URL 생성 오류: " + e.getMessage());
                }
                boolean myLike = recordedLectureLikeRepository.existsByLectureAndUser(lecture,
                    getUserById(userId));
                return DetailedTeacherDto.LectureDto.builder()
                    .recordedId(lecture.getId().toString())
                    .recordTitle(lecture.getTitle())
                    .recordThumbnail(recordThumbnail)
                    .recordThumbnailSmall(recordThumbnailSmall)
                    .likeCount((int) lecture.getLikeCount())
                    .myLike(myLike)
                    .build();
            })
            .sorted(Comparator.comparing(DetailedTeacherDto.LectureDto::getRecordedId).reversed())
            .collect(Collectors.toList());

        return DetailedTeacherDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .profileImageUrl(profileImageUrl)
            .profileImageUrlSmall(profileImageUrlSmall)
            .content(user.getContent())
            .hashtags(user.getHashtags().stream().map(Hashtag::getName).collect(Collectors.toSet()))
            .recordedLectures(sortedRecordedLectures)
            .notices(user.getArticles().stream().map(article -> {
                    String imageUrl = article.getImageUrl();
                    String imageUrlSmall = article.getImageUrlSmall();
                    try {
                        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.contains(
                            "X-Amz-Algorithm")) {
                            imageUrl = s3Service.generatePresignedUrl(article.getImageUrl(),
                                URL_EXPIRATION_SECONDS);
                        }
                        if (imageUrlSmall != null && !imageUrlSmall.isEmpty()
                            && !imageUrlSmall.contains("X-Amz-Algorithm")) {
                            imageUrlSmall = s3Service.generatePresignedUrl(article.getImageUrlSmall(),
                                URL_EXPIRATION_SECONDS);
                        }
                    } catch (Exception e) {
                        System.err.println("공지 Presigned URL 생성 오류: " + e.getMessage());
                    }
                    return DetailedTeacherDto.NoticeDto.builder()
                        .articleId(article.getArticleId().toString())
                        .content(article.getContent())
                        .imageUrl(imageUrl)
                        .imageUrlSmall(imageUrlSmall)
                        .createdAt(article.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()
                            .toEpochMilli())
                        .updatedAt(article.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant()
                            .toEpochMilli())
                        .userName(user.getNickname())
                        .profileImageUrl(finalProfileImageUrl)
                        .profileImageSmallUrl(finalProfileImageUrlSmall)
                        .build();
                }).sorted(Comparator.comparing(DetailedTeacherDto.NoticeDto::getArticleId).reversed())
                .collect(Collectors.toList()))
            .likeCount(user.getTeacherLikes().size())
            .liked(likedByUser)
            .build();
    }


    /**
     * 좋아요 상태를 토글합니다.
     *
     * @param teacherId 강사 ID
     * @param userId    사용자 ID
     * @return 좋아요 상태 (true: 좋아요 추가, false: 좋아요 취소)
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean toggleLike(int teacherId, int userId) {
        Users teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("강사를 찾을 수 없습니다."));
        Users user = teacherRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        TeacherLike existingLike = teacherLikeRepository.findByTeacherAndUser(teacher, user);
        if (existingLike != null) {
            teacherLikeRepository.delete(existingLike);
            return false; // 좋아요 취소
        } else {
            TeacherLike like = new TeacherLike();
            like.setTeacher(teacher);
            like.setUser(user);
            teacherLikeRepository.save(like);
            return true; // 좋아요 추가
        }
    }

    /**
     * 정렬된 강사 정보를 조회합니다.
     *
     * @param sorting 정렬 기준 (0: 최신순, 1: 인기순)
     * @param userId  사용자 ID
     * @param keyword 검색 키워드
     * @return 정렬된 강사 정보 리스트
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<TeacherDto> getSortedTeachers(int sorting, int userId, String keyword) {
        System.out.println(
            "Entering getSortedTeachers with sorting: " + sorting + ", userId: " + userId
                + ", keyword: " + keyword);

        List<Users> users = teacherRepository.findAllTeachers();

        // Check if the keyword filter is working properly
        List<Users> filteredUsers = users.stream()
            .filter(user -> keyword == null || keyword.isEmpty() ||
                user.getNickname().contains(keyword) ||
                user.getEmail().contains(keyword) ||
                (user.getContent() != null && user.getContent().contains(keyword)) ||
                // Added null check for content
                user.getHashtags().stream()
                    .anyMatch(hashtag -> hashtag.getName().contains(keyword)))
            .collect(Collectors.toList());

        if (filteredUsers.isEmpty()) {
            System.out.println("No users found with the given keyword: " + keyword);
        }

        List<TeacherDto> result = filteredUsers.stream()
            .map(user -> {
                String profileImageUrl = null;
                String profileImageUrlSmall = null;
                try {
                    if (user.getProfile_image_url() != null) {
                        profileImageUrl = s3Service.generatePresignedUrl(
                            user.getProfile_image_url(), URL_EXPIRATION_SECONDS);
                    }
                    if (user.getProfile_image_url_small() != null) {
                        profileImageUrlSmall = s3Service.generatePresignedUrl(
                            user.getProfile_image_url_small(), URL_EXPIRATION_SECONDS);
                    }
                } catch (Exception e) {
                    System.err.println("Presigned URL 생성 오류: " + e.getMessage());
                }

                boolean likedByUser =
                    teacherLikeRepository.findByTeacherAndUser(user, getUserById(userId)) != null;

                return TeacherDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .profileImageUrl(profileImageUrl)
                    .profileImageUrlSmall(profileImageUrlSmall)
                    .content(user.getContent())
                    .hashtags(user.getHashtags().stream().map(Hashtag::getName)
                        .collect(Collectors.toSet()))
                    .liked(likedByUser)
                    .likeCount(user.getTeacherLikes().size())
                    .build();
            })
            .sorted((user1, user2) -> {
                if (sorting == 0) {
                    return Integer.compare(user2.getId(), user1.getId());
                } else {
                    return Integer.compare(user2.getLikeCount(), user1.getLikeCount());
                }
            })
            .collect(Collectors.toList());

        System.out.println("Exiting getSortedTeachers with result size: " + result.size());

        return result;
    }

    /**
     * 검색 조건에 맞는 강사 정보를 조회합니다.
     *
     * @param filter  필터 조건
     * @param userId  사용자 ID
     * @param keyword 검색 키워드
     * @return 강사 정보 리스트
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<TeacherDto> searchTeachers(TeacherFilter filter, int userId, String keyword) {
        List<Users> users = teacherRepository.findAllTeachers();

        return users.stream()
            .filter(user -> user.getNickname().contains(keyword) ||
                user.getEmail().contains(keyword) ||
                user.getHashtags().stream()
                    .anyMatch(hashtag -> hashtag.getName().contains(keyword)))
            .map(user -> {
                String profileImageUrl = null;
                String profileImageUrlSmall = null;
                try {
                    if (user.getProfile_image_url() != null) {
                        profileImageUrl = s3Service.generatePresignedUrl(
                            user.getProfile_image_url(), URL_EXPIRATION_SECONDS);
                    }
                    if (user.getProfile_image_url_small() != null) {
                        profileImageUrlSmall = s3Service.generatePresignedUrl(
                            user.getProfile_image_url_small(), URL_EXPIRATION_SECONDS);
                    }
                } catch (Exception e) {
                    System.err.println("Presigned URL 생성 오류: " + e.getMessage());
                }

                boolean likedByUser =
                    teacherLikeRepository.findByTeacherAndUser(user, getUserById(userId)) != null;

                return TeacherDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .profileImageUrl(profileImageUrl)
                    .profileImageUrlSmall(profileImageUrlSmall)
                    .content(user.getContent())
                    .hashtags(user.getHashtags().stream().map(Hashtag::getName)
                        .collect(Collectors.toSet()))
                    .liked(likedByUser)
                    .likeCount(user.getTeacherLikes().size())
                    .build();
            })
            .collect(Collectors.toList());
    }

    /**
     * 해시태그로 강사 정보를 조회합니다.
     *
     * @param hashtag 해시태그
     * @param userId  사용자 ID
     * @return 강사 정보 리스트
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<TeacherDto> searchTeachersByHashtag(String hashtag, int userId) {
        List<Users> users = teacherRepository.findTeachersByHashtag(hashtag);

        return users.stream()
            .map(user -> {
                String profileImageUrl = null;
                String profileImageUrlSmall = null;
                try {
                    if (user.getProfile_image_url() != null) {
                        profileImageUrl = s3Service.generatePresignedUrl(
                            user.getProfile_image_url(), URL_EXPIRATION_SECONDS);
                    }
                    if (user.getProfile_image_url_small() != null) {
                        profileImageUrlSmall = s3Service.generatePresignedUrl(
                            user.getProfile_image_url_small(), URL_EXPIRATION_SECONDS);
                    }
                } catch (Exception e) {
                    System.err.println("Presigned URL 생성 오류: " + e.getMessage());
                }

                boolean likedByUser =
                    teacherLikeRepository.findByTeacherAndUser(user, getUserById(userId)) != null;

                return TeacherDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .profileImageUrl(profileImageUrl)
                    .profileImageUrlSmall(profileImageUrlSmall)
                    .content(user.getContent())
                    .hashtags(user.getHashtags().stream().map(Hashtag::getName)
                        .collect(Collectors.toSet()))
                    .liked(likedByUser)
                    .likeCount(user.getTeacherLikes().size())
                    .build();
            })
            .collect(Collectors.toList());
    }

    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    private Users getUserById(int userId) {
        return teacherRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
}
