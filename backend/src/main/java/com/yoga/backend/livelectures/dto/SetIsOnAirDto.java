package com.yoga.backend.livelectures.dto;

public class SetIsOnAirDto {

    private long liveId;
    private Boolean onAir;

    public long getLiveId() {
        return liveId;
    }

    public void setLiveId(long liveId) {
        this.liveId = liveId;
    }

    public Boolean getOnAir() {
        return onAir;
    }

    public void setOnAir(Boolean onAir) {
        this.onAir = onAir;
    }
}
