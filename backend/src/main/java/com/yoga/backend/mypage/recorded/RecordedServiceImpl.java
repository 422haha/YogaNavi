package com.yoga.backend.mypage.recorded;

import com.yoga.backend.common.awsS3.S3Service;
import com.yoga.backend.common.entity.RecordedLectures.RecordedLecture;
import com.yoga.backend.common.entity.RecordedLectures.RecordedLectureChapter;
import com.yoga.backend.mypage.recorded.dto.ChapterDto;
import com.yoga.backend.mypage.recorded.dto.LectureCreationStatus;
import com.yoga.backend.mypage.recorded.dto.LectureDto;
import com.yoga.backend.mypage.recorded.repository.MyLikeLectureListRepository;
import com.yoga.backend.mypage.recorded.repository.RecordedLectureListRepository;
import com.yoga.backend.mypage.recorded.repository.RecordedLectureRepository;
import jakarta.transaction.Transactional;
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

@Service
public class RecordedServiceImpl implements RecordedService {

    @Autowired
    private RecordedLectureListRepository recordedLectureListRepository;

    @Autowired
    private RecordedLectureRepository recordedLectureRepository;

    @Autowired
    private MyLikeLectureListRepository myLikeLectureListRepository;

    @Autowired
    @Qualifier("applicationTaskExecutor")
    private AsyncTaskExecutor taskExecutor;

    private final Map<String, CompletableFuture<LectureDto>> ongoingTasks = new ConcurrentHashMap<>();

    @Autowired
    private S3Service s3Service;

    @Override
    public List<LectureDto> getMyLectures(String email) {
        return recordedLectureListRepository.findAllLectures(email);
    }

    @Override
    public List<LectureDto> getLikeLectures(String email) {
        return myLikeLectureListRepository.findMyLikedLectures(email);
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
            chapter.setThumbnail(chapterDto.getThumbnailUrl());
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
    public LectureDto getLectureDetails(Long recordedId, String email) {
        RecordedLecture lecture = recordedLectureRepository.findById(recordedId)
            .orElseThrow(() -> new RuntimeException("Lecture not found"));

        return convertToDto(lecture);
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
            chapterDto.setThumbnailUrl(chapter.getThumbnail());
            chapterDto.setVideoUrl(chapter.getVideoUrl());
            chapterDto.setChapterNumber(chapter.getChapterNumber());
            chapterDtos.add(chapterDto);
        }
        dto.setRecordedLectureChapters(chapterDtos);

        return dto;
    }
}