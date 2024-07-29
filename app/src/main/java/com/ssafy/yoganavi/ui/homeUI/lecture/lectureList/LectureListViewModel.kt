package com.ssafy.yoganavi.ui.homeUI.lecture.lectureList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ssafy.yoganavi.data.repository.InfoRepository
import com.ssafy.yoganavi.data.repository.LectureRepository
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import com.ssafy.yoganavi.ui.homeUI.lecture.lectureList.lecture.SortAndKeyword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LectureListViewModel @Inject constructor(
    private val lectureRepository: LectureRepository,
    private val infoRepository: InfoRepository,
) : ViewModel() {

    private val _sortAndKeyword = MutableStateFlow(SortAndKeyword())

    @OptIn(ExperimentalCoroutinesApi::class)
    val lectureList: Flow<PagingData<LectureData>> = _sortAndKeyword
        .flatMapLatest { (sort, keyword, searchInTitle, searchInContent) ->
            lectureRepository.getLectureList(
                sort = sort,
                keyword = keyword,
                searchInTitle = searchInTitle,
                searchInContent = searchInContent
            )
        }.cachedIn(viewModelScope)

    fun setLectureLike(recordedId: Long) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.likeLecture(recordedId) }
            .onFailure { it.printStackTrace() }
    }

    fun updateSortAndKeyword(
        sort: String = _sortAndKeyword.value.sort,
        keyword: String? = _sortAndKeyword.value.keyword,
        searchInTitle: Boolean = _sortAndKeyword.value.searchInTitle,
        searchInContent: Boolean = _sortAndKeyword.value.searchInContent,
    ) {
        val newValue = SortAndKeyword(sort, keyword, searchInTitle, searchInContent)
        Timber.d("NEW VALUE : ${newValue}")
        _sortAndKeyword.value = newValue
    }
}
