package com.yoga.backend.mypage.recorded;

import com.yoga.backend.common.awsS3.S3Service;
import com.yoga.backend.common.entity.RecordedLectures.RecordedLecture;
import com.yoga.backend.common.entity.RecordedLectures.RecordedLectureChapter;
import com.yoga.backend.common.entity.RecordedLectures.RecordedLectureLike;
import com.yoga.backend.mypage.recorded.dto.ChapterDto;
import com.yoga.backend.mypage.recorded.dto.DeleteDto;
import com.yoga.backend.mypage.recorded.dto.LectureDto;
import com.yoga.backend.mypage.recorded.repository.MyLikeLectureListRepository;
import com.yoga.backend.mypage.recorded.repository.RecordedLectureLikeRepository;
import com.yoga.backend.mypage.recorded.repository.RecordedLectureListRepository;
import com.yoga.backend.mypage.recorded.repository.RecordedLectureRepository;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 녹화 강의 관련 비즈니스 로직을 처리하는 서비스 구현 클래스.
 * 강의 목록 조회, 강의 생성, 수정, 삭제 및 좋아요 기능을 제공.
 */
@Slf4j
@Service
public class RecordedServiceImpl implements RecordedService {

    private static final String S3_BASE_URL = "https://yoga-navi.s3.ap-northeast-2.amazonaws.com/";
    private static final long URL_EXPIRATION_SECONDS = 86400; // 1 hour

    @Autowired
    private RecordedLectureListRepository recordedLectureListRepository;

    @Autowired
    private RecordedLectureRepository recordedLectureRepository;

    @Autowired
    private MyLikeLectureListRepository myLikeLectureListRepository;

    @Autowired
    private RecordedLectureLikeRepository lectureLikeRepository;

    @Autowired
    private S3Service s3Service;

    /**
     * 사용자가 업로드한 강의 목록을 조회
     *
     * @param userId 사용자 id
     * @return 사용자가 업로드한 강의 목록 (LectureDto 리스트)
     */
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<LectureDto> getMyLectures(int userId) {
        List<LectureDto> lectures = recordedLectureListRepository.findAllLectures(userId);
        return applyPresignedUrls(lectures);
    }

    /**
     * 사용자가 좋아요한 강의 목록 조회
     *
     * @param userId 사용자 id
     * @return 사용자가 좋아요한 강의 목록 (LectureDto 리스트)
     */
    @Override
    @Transactional(readOnly = true)
    public List<LectureDto> getLikeLectures(int userId) {
        List<LectureDto> lectures = myLikeLectureListRepository.findMyLikedLectures(userId);
        return generatePresignedUrlsLike(lectures);
    }

    /**
     * 새로운 강의 업로드
     *
     * @param lectureDto 사용자가 저장할 강의 dto
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void saveLecture(LectureDto lectureDto) {
        RecordedLecture lecture = new RecordedLecture();
        lecture.setUserId(lectureDto.getUserId());
        lecture.setTitle(lectureDto.getRecordTitle());
        lecture.setContent(lectureDto.getRecordContent());
        lecture.setThumbnail(lectureDto.getRecordThumbnail());

        List<RecordedLectureChapter> chapters = new ArrayList<>();
        for (ChapterDto chapterDto : lectureDto.getRecordedLectureChapters()) {
            RecordedLectureChapter chapter = new RecordedLectureChapter();
            chapter.setTitle(chapterDto.getChapterTitle());
            chapter.setDescription(chapterDto.getChapterDescription());
            chapter.setVideoUrl(chapterDto.getRecordVideo());
            chapter.setLecture(lecture);
            chapter.setChapterNumber(chapterDto.getChapterNumber());
            chapters.add(chapter);
        }
        lecture.setChapters(chapters);
        recordedLectureRepository.save(lecture);
    }

    /**
     * 강의 내용 상세 조회
     *
     * @param recordedId 강의 id
     * @param userId 사용자 id
     * @return 사용자가 업로드한 강의 상세 정보
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public LectureDto getLectureDetails(Long recordedId, int userId) {
        RecordedLecture lecture = recordedLectureRepository.findById(recordedId)
            .orElseThrow(() -> new RuntimeException("강의 찾을 수 없음"));

        LectureDto dto = convertToDto(lecture);
        dto.setLikeCount(lecture.getLikeCount());
        dto.setMyLike(lectureLikeRepository.existsByLectureIdAndUserId(recordedId, userId));

        // Presigned URL 생성 및 적용
        return applyPresignedUrls(Collections.singletonList(dto)).get(0);
    }

    /**
     * 강의 세부내용 업데이트
     *
     * @param lectureDto 업데이트할 내용 dto
     * @return 업데이트 결과
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean updateLecture(LectureDto lectureDto) {
        try {
            RecordedLecture lecture = recordedLectureRepository.findById(lectureDto.getRecordedId())
                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));

            if (lecture.getUserId() != lectureDto.getUserId()) {
                throw new RuntimeException("해당 강의를 수정할 권한이 없습니다.");
            }

            updateLectureDetails(lecture, lectureDto);
            updateChapters(lecture, lectureDto.getRecordedLectureChapters());

            recordedLectureRepository.save(lecture);
            return true;
        } catch (Exception e) {
            log.error("강의 수정 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    private void updateLectureDetails(RecordedLecture lecture, LectureDto lectureDto) {
        if (!lecture.getTitle().equals(lectureDto.getRecordTitle())) {
            lecture.setTitle(lectureDto.getRecordTitle());
            log.info("강의 제목 업데이트: {}", lectureDto.getRecordTitle());
        }

        if (!lecture.getContent().equals(lectureDto.getRecordContent())) {
            lecture.setContent(lectureDto.getRecordContent());
            log.info("강의 내용 업데이트");
        }

        if (!lecture.getThumbnail().equals(lectureDto.getRecordThumbnail())) {
            s3Service.deleteFile(lecture.getThumbnail());
            lecture.setThumbnail(lectureDto.getRecordThumbnail());
            log.info("강의 썸네일 업데이트: {}", lectureDto.getRecordThumbnail());
        }
    }

    private void updateChapters(RecordedLecture lecture, List<ChapterDto> chapterDtos) {
        Map<Long, RecordedLectureChapter> existingChapters = lecture.getChapters().stream()
            .collect(Collectors.toMap(RecordedLectureChapter::getId, Function.identity()));

        List<RecordedLectureChapter> updatedChapters = new ArrayList<>();

        for (ChapterDto chapterDto : chapterDtos) {
            if (chapterDto.getId() != 0 && existingChapters.containsKey(chapterDto.getId())) {
                // 기존 챕터 수정
                RecordedLectureChapter chapter = existingChapters.get(chapterDto.getId());
                updateChapter(chapter, chapterDto);
                updatedChapters.add(chapter);
                existingChapters.remove(chapterDto.getId());
                log.info("챕터 업데이트: {}", chapter.getId());
            } else {
                // 새 챕터 추가
                RecordedLectureChapter newChapter = createChapter(chapterDto, lecture);
                updatedChapters.add(newChapter);
                log.info("새 챕터 추가: {}", newChapter.getTitle()); // ID 대신 제목 로깅
            }
        }

        // 삭제된 챕터 처리
        for (RecordedLectureChapter removedChapter : existingChapters.values()) {
            if (removedChapter.getVideoUrl() != null && !removedChapter.getVideoUrl().isEmpty()) {
                s3Service.deleteFile(removedChapter.getVideoUrl());
            }
            log.info("챕터 삭제: {}", removedChapter.getId());
        }

        lecture.getChapters().clear(); // 기존 챕터 목록 비우기
        lecture.getChapters().addAll(updatedChapters); // 업데이트된 챕터 목록 추가
    }

    private void updateChapter(RecordedLectureChapter chapter, ChapterDto chapterDto) {
        if (!chapter.getTitle().equals(chapterDto.getChapterTitle())) {
            chapter.setTitle(chapterDto.getChapterTitle());
            log.info("챕터 제목 업데이트: {}", chapter.getId());
        }

        if (!Objects.equals(chapter.getDescription(), chapterDto.getChapterDescription())) {
            chapter.setDescription(chapterDto.getChapterDescription());
            log.info("챕터 설명 업데이트: {}", chapter.getId());
        }

        if (chapter.getChapterNumber() != chapterDto.getChapterNumber()) {
            chapter.setChapterNumber(chapterDto.getChapterNumber());
            log.info("챕터 번호 업데이트: {}", chapter.getId());
        }

        if (!Objects.equals(chapter.getVideoUrl(), chapterDto.getRecordVideo())) {
            if (chapter.getVideoUrl() != null && !chapter.getVideoUrl().isEmpty()) {
                s3Service.deleteFile(chapter.getVideoUrl());
            }
            chapter.setVideoUrl(chapterDto.getRecordVideo());
            log.info("챕터 비디오 URL 업데이트: {}", chapter.getId());
        }
    }

    private RecordedLectureChapter createChapter(ChapterDto chapterDto, RecordedLecture lecture) {
        RecordedLectureChapter chapter = new RecordedLectureChapter();
        chapter.setTitle(chapterDto.getChapterTitle());
        chapter.setDescription(chapterDto.getChapterDescription());
        chapter.setChapterNumber(chapterDto.getChapterNumber());
        chapter.setVideoUrl(chapterDto.getRecordVideo());
        chapter.setLecture(lecture);
        return chapter;
    }

    /**
     * 사용자가 업로드한 강의 목록을 조회합니다.
     *
     * @param userId 사용자 id
     * @return 사용자가 업로드한 강의 목록 (LectureDto 리스트)
     */
    @Override
    @Transactional
    public void deleteLectures(DeleteDto deleteDto, int userId) {
        log.info("이 id를 가진 강의들을 삭제: {}", deleteDto);

        if (deleteDto.getLectureIds() == null || deleteDto.getLectureIds().isEmpty()) {
            log.warn("삭제를 위해서는 강의 id가 필요함. 강의 id가 존재하지 않음");
            throw new RuntimeException("삭제할 강의가 지정되지 않았습니다.");
        }

        try {
            List<RecordedLecture> lectures = recordedLectureRepository.findAllByIdCustom(
                deleteDto.getLectureIds());

            if (lectures.isEmpty()) {
                log.warn("삭제할 강의가 존재하지 않음");
                throw new RuntimeException("삭제할 강의를 찾을 수 없습니다.");
            }

            List<Long> deletedLectureIds = new ArrayList<>();
            List<Long> notFoundLectureIds = new ArrayList<>(deleteDto.getLectureIds());

            for (RecordedLecture lecture : lectures) {
                if (lecture.getUserId() != userId) {
                    log.error("사용자 {} 는 이 강의를 삭제할 권한이 없음 {}", userId, lecture.getId());
                    throw new RuntimeException("강의 ID " + lecture.getId() + "에 대한 삭제 권한이 없습니다.");
                }

                log.info("강의의 s3파일 삭제중: {}", lecture.getId());
                deleteS3Files(lecture);

                log.info("db에서 강의 삭제중: {}", lecture.getId());
                recordedLectureRepository.delete(lecture);

                deletedLectureIds.add(lecture.getId());
                notFoundLectureIds.remove(lecture.getId());
            }

            log.info("강의 삭제 완료: {}", deletedLectureIds);
            if (!notFoundLectureIds.isEmpty()) {
                log.warn("강의를 찾을 수 없음: {}", notFoundLectureIds);
            }
        } catch (Exception e) {
            log.error("강의 삭제중 에러 발생", e);
            throw new RuntimeException("강의 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void deleteS3Files(RecordedLecture lecture) {
        try {
            if (lecture.getThumbnail() != null && !lecture.getThumbnail().isEmpty()) {
                log.info("강의 썸네일 삭제 {}: {}", lecture.getId(), lecture.getThumbnail());
                s3Service.deleteFile(lecture.getThumbnail());
            }

            for (RecordedLectureChapter chapter : lecture.getChapters()) {
                if (chapter.getVideoUrl() != null && !chapter.getVideoUrl().isEmpty()) {
                    log.info("챕터의 비디오 삭제 {} 강의 번호 {}: {}",
                        chapter.getId(), lecture.getId(), chapter.getVideoUrl());
                    s3Service.deleteFile(chapter.getVideoUrl());
                }
            }
        } catch (Exception e) {
            log.error("강의의 s3파일 삭제중 에러 발생: {}", lecture.getId(), e);
        }
    }

    /**
     * 좋아요/좋아요 취소
     *
     * @param recordedId 강의 id
     * @param userId 사용자 id
     * @return false: 좋아요 취소/ true: 좋아요 성공
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean toggleLike(Long recordedId, int userId) {
        RecordedLecture lecture = recordedLectureRepository.findById(recordedId)
            .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));

        boolean exists = lectureLikeRepository.existsByLectureIdAndUserId(recordedId, userId);
        if (exists) {
            lectureLikeRepository.deleteByLectureIdAndUserId(recordedId, userId);
            lecture.decrementLikeCount();
            log.info("강의 {}의 좋아요가 사용자 {}에 의해 취소됨", recordedId, userId);
            return false;
        } else {
            RecordedLectureLike like = new RecordedLectureLike();
            like.setLecture(lecture);
            like.setUserId(userId);
            lectureLikeRepository.save(like);
            lecture.incrementLikeCount();
            log.info("강의 {}의 좋아요가 사용자 {}에 의해 추가됨", recordedId, userId);
            return true;
        }
    }

    private LectureDto convertToDto(RecordedLecture lecture) {
        LectureDto dto = new LectureDto();
        dto.setRecordedId(lecture.getId());
        dto.setUserId(lecture.getUserId());
        dto.setRecordTitle(lecture.getTitle());
        dto.setRecordContent(lecture.getContent());
        dto.setRecordThumbnail(lecture.getThumbnail());
        dto.setLikeCount(lecture.getLikeCount());
        dto.setMyLike(false); // 이 값은 나중에 설정됩니다.

        List<ChapterDto> chapterDtos = new ArrayList<>();
        for (RecordedLectureChapter chapter : lecture.getChapters()) {
            ChapterDto chapterDto = new ChapterDto();
            chapterDto.setId(chapter.getId());
            chapterDto.setChapterTitle(chapter.getTitle());
            chapterDto.setChapterDescription(chapter.getDescription());
            chapterDto.setChapterNumber(chapter.getChapterNumber());
            chapterDto.setRecordVideo(chapter.getVideoUrl());
            chapterDtos.add(chapterDto);
        }
        dto.setRecordedLectureChapters(chapterDtos);

        return dto;
    }

    private List<LectureDto> generatePresignedUrlsLike(List<LectureDto> lectures) {
        for (LectureDto lecture : lectures) {
            lecture.setRecordThumbnail(s3Service.generatePresignedUrl(lecture.getRecordThumbnail(),
                URL_EXPIRATION_SECONDS)); // 1 hour expiration
        }
        return lectures;
    }

    private List<LectureDto> applyPresignedUrls(List<LectureDto> lectures) {
        Map<String, String> presignedUrls = generatePresignedUrls(lectures);

        for (LectureDto lecture : lectures) {
            lecture.setRecordThumbnail(
                getPresignedUrl(lecture.getRecordThumbnail(), presignedUrls));
            if (lecture.getRecordedLectureChapters() != null) {
                for (ChapterDto chapter : lecture.getRecordedLectureChapters()) {
                    chapter.setRecordVideo(
                        getPresignedUrl(chapter.getRecordVideo(), presignedUrls));
                }
            }
        }

        return lectures;
    }

    private Map<String, String> generatePresignedUrls(List<LectureDto> lectures) {
        Set<String> keysToGenerate = new HashSet<>();

        for (LectureDto lecture : lectures) {
            addKeyIfNeeded(keysToGenerate, lecture.getRecordThumbnail());
            if (lecture.getRecordedLectureChapters() != null) {
                for (ChapterDto chapter : lecture.getRecordedLectureChapters()) {
                    addKeyIfNeeded(keysToGenerate, chapter.getRecordVideo());
                }
            }
        }

        if (keysToGenerate.isEmpty()) {
            return Collections.emptyMap();
        }

        return s3Service.generatePresignedUrls(keysToGenerate, URL_EXPIRATION_SECONDS);
    }


    private void addKeyIfNeeded(Set<String> keysToGenerate, String url) {
        if (url != null && url.startsWith(S3_BASE_URL)) {
            String key = url.substring(S3_BASE_URL.length());
            keysToGenerate.add(key);
        }
    }

    private String getPresignedUrl(String url, Map<String, String> presignedUrls) {
        if (url != null && url.startsWith(S3_BASE_URL)) {
            String key = url.substring(S3_BASE_URL.length());
            return presignedUrls.getOrDefault(key, url);
        }
        return url;
    }

}