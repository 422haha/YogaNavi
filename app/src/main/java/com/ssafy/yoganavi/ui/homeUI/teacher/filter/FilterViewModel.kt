package com.ssafy.yoganavi.ui.homeUI.teacher.filter

import androidx.lifecycle.ViewModel
import com.ssafy.yoganavi.data.source.teacher.FilterData
import com.ssafy.yoganavi.ui.utils.Week

class FilterViewModel : ViewModel() {
    var filter = FilterData()
    var dayStatusMap = Week.entries.associateWith { false }.toMutableMap()
}