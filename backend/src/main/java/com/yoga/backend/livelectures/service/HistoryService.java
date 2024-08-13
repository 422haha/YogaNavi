package com.yoga.backend.livelectures.service;

import com.yoga.backend.livelectures.dto.LectureHistoryDto;
import java.util.List;

public interface HistoryService {

    List<LectureHistoryDto> getHistory(int userId);
}
