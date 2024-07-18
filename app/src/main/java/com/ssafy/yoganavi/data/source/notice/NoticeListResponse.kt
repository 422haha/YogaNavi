package com.ssafy.yoganavi.data.source.notice

import com.ssafy.yoganavi.data.source.live.LiveRecordData

data class NoticeListResponse(
    val message : String,
    val data : List<NoticeData>
)