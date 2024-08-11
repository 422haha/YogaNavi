package com.yoga.backend.livelectures.service;


import com.yoga.backend.livelectures.dto.HomeResponseDto;
import java.util.List;

public interface HomeService {

    List<HomeResponseDto> getHomeData(int userId);

    boolean updateLiveState(Long liveId, Boolean isOnAir);
}
