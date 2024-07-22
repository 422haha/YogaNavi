package com.yoga.backend.mypage.recorded;

import com.yoga.backend.common.awsS3.S3Service;
import com.yoga.backend.common.entity.RecordedLectures.RecordedLecture;
import com.yoga.backend.common.entity.RecordedLectures.RecordedLectureChapter;
import com.yoga.backend.common.entity.RecordedLectures.RecordedLectureLike;
import com.yoga.backend.mypage.recorded.dto.ChapterDto;
import com.yoga.backend.mypage.recorded.dto.LectureCreationStatus;
import com.yoga.backend.mypage.recorded.dto.LectureDto;
import com.yoga.backend.mypage.recorded.repository.MyLikeLectureListRepository;
import com.yoga.backend.mypage.recorded.repository.RecordedLectureLikeRepository;
import com.yoga.backend.mypage.recorded.repository.RecordedLectureListRepository;
import com.yoga.backend.mypage.recorded.repository.RecordedLectureRepository;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 녹화 강의 관련 비즈니스 로직을 처리하는 서비스 구현 클래스.
 * 이 클래스는 강의 목록 조회, 강의 생성, 수정, 삭제 및 좋아요 기능을 제공.
 */
@Service
public class RecordedServiceImpl implements RecordedService {

    @Autowired
    private RecordedLectureListRepository recordedLectureListRepository;

    @Autowired
    private RecordedLectureRepository recordedLectureRepository;

    @Autowired
    private MyLikeLectureListRepository myLikeLectureListRepository;

    @Autowired
    private RecordedLectureLikeRepository lectureLikeRepository;

    @Autowired
    @Qualifier("applicationTaskExecutor")
    private AsyncTaskExecutor taskExecutor;

    private final Map<String, CompletableFuture<LectureDto>> ongoingTasks = new ConcurrentHashMap<>();

    @Autowired
    private S3Service s3Service;

    /**
     * 사용자가 업로드한 강의 목록을 조회
     *
     * @param email 사용자 이메일
     * @return 사용자가 업로드한 강의 목록 (LectureDto 리스트)
     */
    @Override
    @Transactional(readOnly = true)
    public List<LectureDto> getMyLectures(String email) {
        List<LectureDto> lectures = recordedLectureListRepository.findAllLectures(email);
        return generatePresignedUrls(lectures);
    }

    /**
     * 사용자가 좋아요한 강의 목록을 조회
     *
     * @param email 사용자 이메일
     * @return 사용자가 업로드한 강의 목록 (LectureDto 리스트)
     */
    @Override
    @Transactional(readOnly = true)
    public List<LectureDto> getLikeLectures(String email) {
        List<LectureDto> lectures = myLikeLectureListRepository.findMyLikedLectures(email);
        return generatePresignedUrls(lectures);
    }

    /**
     * 강의를 비동기적으로 저장
     *
     * @param lectureDto 강의 정보
     * @param sessionId 세션 ID
     * @return 저장된 강의의 CompletableFuture
     */
    @Override
    public CompletableFuture<LectureDto> saveLectureAsync(LectureDto lectureDto, String sessionId) {
        CompletableFuture<LectureDto> future = CompletableFuture.supplyAsync(() -> {
            try {
                return saveLecture(lectureDto);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, taskExecutor);

        ongoingTasks.put(sessionId, future);
        return future;
    }

    /**
     * 강의를 저장
     *
     * @param lectureDto 강의 정보
     * @return 저장된 강의 정보
     */
    @Override
    @Transactional
    public LectureDto saveLecture(LectureDto lectureDto) {
        RecordedLecture lecture = new RecordedLecture();
        lecture.setEmail(lectureDto.getEmail());
        lecture.setTitle(lectureDto.getRecordTitle());
        lecture.setContent(lectureDto.getRecordContent());
        lecture.setThumbnail(lectureDto.getRecordThumbnail());

        List<RecordedLectureChapter> chapters = new ArrayList<>();
        for (ChapterDto chapterDto : lectureDto.getRecordedLectureChapters()) {
            RecordedLectureChapter chapter = new RecordedLectureChapter();
            chapter.setTitle(chapterDto.getChapterTitle());
            chapter.setDescription(chapterDto.getChapterDescription());
            chapter.setVideoUrl(chapterDto.getVideoUrl());
            chapter.setLecture(lecture);
            chapter.setChapterNumber(chapterDto.getChapterNumber());
            chapters.add(chapter);
        }
        lecture.setChapters(chapters);

        RecordedLecture savedLecture = recordedLectureRepository.save(lecture);
        return convertToDto(savedLecture);
    }

    /**
     * 강의 생성 상태 확인
     *
     * @param sessionId 세션 ID
     * @return 강의 생성 상태
     */
    @Override
    public LectureCreationStatus getLectureCreationStatus(String sessionId) {
        CompletableFuture<LectureDto> future = ongoingTasks.get(sessionId);
        if (future == null) {
            return new LectureCreationStatus("NOT_FOUND", "강의 생성 요청을 찾을 수 없습니다.");
        }

        if (future.isDone()) {
            try {
                LectureDto lectureDto = future.get();
                ongoingTasks.remove(sessionId);
                return new LectureCreationStatus("COMPLETED", "강의 생성이 완료되었습니다.", lectureDto);
            } catch (Exception e) {
                ongoingTasks.remove(sessionId);
                return new LectureCreationStatus("FAILED", "강의 생성 중 오류가 발생했습니다: " + e.getMessage());
            }
        } else {
            return new LectureCreationStatus("IN_PROGRESS", "강의 생성이 진행 중입니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LectureDto getLectureDetails(Long recordedId, String email) {
        RecordedLecture lecture = recordedLectureRepository.findById(recordedId)
            .orElseThrow(() -> new RuntimeException("Lecture not found"));

        LectureDto dto = convertToDto(lecture);
        dto.setLikeCount(lecture.getLikeCount());
        dto.setMyLike(lectureLikeRepository.existsByLectureIdAndUserEmail(recordedId, email));

        // Generate presigned URLs
        dto.setRecordThumbnail(s3Service.generatePresignedUrl(dto.getRecordThumbnail(), 3600)); // 1 hour expiration

        for (ChapterDto chapterDto : dto.getRecordedLectureChapters()) {
            chapterDto.setVideoUrl(s3Service.generatePresignedUrl(chapterDto.getVideoUrl(), 3600));
        }

        return dto;
    }

    @Override
    @Transactional
    public LectureDto updateLecture(Long lectureId, LectureDto lectureDto, String email) {
        RecordedLecture lecture = recordedLectureRepository.findById(lectureId)
            .orElseThrow(() -> new RuntimeException("강의가 없습니다."));

        if (!lecture.getEmail().equals(email)) {
            throw new RuntimeException("강의를 수정할 수 없습니다.");
        }

        updateLectureDetails(lecture, lectureDto);
        updateChapters(lecture, lectureDto.getRecordedLectureChapters());

        RecordedLecture updatedLecture = recordedLectureRepository.save(lecture);
        return convertToDto(updatedLecture);
    }

    private void updateLectureDetails(RecordedLecture lecture, LectureDto lectureDto) {
        lecture.setTitle(lectureDto.getRecordTitle());
        lecture.setContent(lectureDto.getRecordContent());

        if (!lecture.getThumbnail().equals(lectureDto.getRecordThumbnail())) {
            String oldThumbnail = lecture.getThumbnail();
            lecture.setThumbnail(lectureDto.getRecordThumbnail());
            s3Service.deleteFile(oldThumbnail);
        }
    }

    private void updateChapters(RecordedLecture lecture, List<ChapterDto> chapterDtos) {
        Map<Long, RecordedLectureChapter> existingChapters = lecture.getChapters().stream()
            .collect(Collectors.toMap(RecordedLectureChapter::getId, Function.identity()));

        List<RecordedLectureChapter> updatedChapters = new ArrayList<>();

        for (ChapterDto chapterDto : chapterDtos) {
            RecordedLectureChapter chapter = existingChapters.get(chapterDto.getId());
            if (chapter == null) {
                chapter = new RecordedLectureChapter();
                chapter.setLecture(lecture);
            }

            updateChapter(chapter, chapterDto);
            updatedChapters.add(chapter);
            existingChapters.remove(chapter.getId());
        }

        // 삭제된 챕터 처리
        for (RecordedLectureChapter removedChapter : existingChapters.values()) {
            s3Service.deleteFile(removedChapter.getVideoUrl());
        }

        lecture.setChapters(updatedChapters);
    }

    private void updateChapter(RecordedLectureChapter chapter, ChapterDto chapterDto) {
        chapter.setTitle(chapterDto.getChapterTitle());
        chapter.setDescription(chapterDto.getChapterDescription());
        chapter.setChapterNumber(chapterDto.getChapterNumber());

        if (!chapter.getVideoUrl().equals(chapterDto.getVideoUrl())) {
            String oldVideo = chapter.getVideoUrl();
            chapter.setVideoUrl(chapterDto.getVideoUrl());
            s3Service.deleteFile(oldVideo);
        }
    }

    @Override
    @Transactional
    public void deleteLecture(Long lectureId, String email) {
        RecordedLecture lecture = recordedLectureRepository.findById(lectureId)
            .orElseThrow(() -> new RuntimeException("강의가 없습니다."));

        if (!lecture.getEmail().equals(email)) {
            throw new RuntimeException("강의를 삭제할 수 없습니다.");
        }

        // 강의 삭제
        recordedLectureRepository.delete(lecture);

        // S3에서 파일 삭제
        s3Service.deleteFile(lecture.getThumbnail());
        lecture.getChapters().forEach(chapter -> {
            s3Service.deleteFile(chapter.getVideoUrl());
        });
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public LectureDto setLike(Long recordedId, String email) {
        return recordedLectureRepository.findById(recordedId)
            .map(lecture -> {
                if (!lectureLikeRepository.existsByLectureIdAndUserEmail(recordedId, email)) {
                    RecordedLectureLike like = new RecordedLectureLike();
                    like.setLecture(lecture);
                    like.setUserEmail(email);
                    lectureLikeRepository.save(like);

                    boolean updated = false;
                    while (!updated) {
                        try {
                            lecture.incrementLikeCount();
                            recordedLectureRepository.save(lecture);
                            updated = true;
                        } catch (OptimisticLockingFailureException e) {
                            // Refresh the entity and retry
                            lecture = recordedLectureRepository.findById(recordedId)
                                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));
                        }
                    }
                }
                return getLectureDetails(recordedId, email);
            })
            .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public LectureDto setDislike(Long recordedId, String email) {
        return recordedLectureRepository.findById(recordedId)
            .map(lecture -> {
                if (lectureLikeRepository.existsByLectureIdAndUserEmail(recordedId, email)) {
                    lectureLikeRepository.deleteByLectureIdAndUserEmail(recordedId, email);

                    boolean updated = false;
                    while (!updated) {
                        try {
                            lecture.decrementLikeCount();
                            recordedLectureRepository.save(lecture);
                            updated = true;
                        } catch (OptimisticLockingFailureException e) {
                            // Refresh the entity and retry
                            lecture = recordedLectureRepository.findById(recordedId)
                                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));
                        }
                    }
                }
                return getLectureDetails(recordedId, email);
            })
            .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));
    }

    private LectureDto convertToDto(RecordedLecture lecture) {
        LectureDto dto = new LectureDto();
        dto.setRecordedId(lecture.getId());
        dto.setEmail(lecture.getEmail());
        dto.setRecordTitle(lecture.getTitle());
        dto.setRecordContent(lecture.getContent());
        dto.setRecordThumbnail(lecture.getThumbnail());
        dto.setLikeCount(0); // 새로 생성된 강의이므로 좋아요 수는 0
        dto.setMyLike(false);

        List<ChapterDto> chapterDtos = new ArrayList<>();
        for (RecordedLectureChapter chapter : lecture.getChapters()) {
            ChapterDto chapterDto = new ChapterDto();
            chapterDto.setId(chapter.getId());
            chapterDto.setChapterTitle(chapter.getTitle());
            chapterDto.setChapterDescription(chapter.getDescription());
            chapterDto.setChapterNumber(chapter.getChapterNumber());
            chapterDto.setVideoUrl(s3Service.generatePresignedUrl(chapter.getVideoUrl(), 3600));
            chapterDtos.add(chapterDto);
        }
        dto.setRecordedLectureChapters(chapterDtos);

        return dto;
    }

    private List<LectureDto> generatePresignedUrls(List<LectureDto> lectures) {
        for (LectureDto lecture : lectures) {
            lecture.setRecordThumbnail(s3Service.generatePresignedUrl(lecture.getRecordThumbnail(), 3600)); // 1 hour expiration
        }
        return lectures;
    }

}