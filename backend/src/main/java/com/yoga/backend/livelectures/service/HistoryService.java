package com.yoga.backend.livelectures.service;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.livelectures.dto.LectureHistoryDto;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface HistoryService {

    List<LectureHistoryDto> getHistory(int userId);

}
