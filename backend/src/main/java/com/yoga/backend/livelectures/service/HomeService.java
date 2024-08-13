package com.yoga.backend.livelectures.service;


import com.yoga.backend.livelectures.dto.HomeResponseDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HomeService {

    List<HomeResponseDto> getHomeData(int userId, int page, int size);

    boolean updateLiveState(Long liveId, Boolean isOnAir);
}
