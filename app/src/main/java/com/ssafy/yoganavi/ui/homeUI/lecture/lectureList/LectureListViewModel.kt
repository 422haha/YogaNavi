package com.ssafy.yoganavi.ui.homeUI.lecture.lectureList

import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.amazonaws.services.s3.AmazonS3Client
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.repository.lecture.LectureRepository
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import com.ssafy.yoganavi.ui.homeUI.lecture.lectureList.lecture.SortAndKeyword
import com.ssafy.yoganavi.ui.utils.loadS3Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureListViewModel @Inject constructor(
    private val lectureRepository: LectureRepository,
    private val infoRepository: InfoRepository,
    private val s3Client: AmazonS3Client
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
            .onSuccess { updateSortAndKeyword(likeChange = true) }
            .onFailure { it.printStackTrace() }
    }

    fun updateSortAndKeyword(
        sort: String = _sortAndKeyword.value.sort,
        keyword: String? = _sortAndKeyword.value.keyword,
        searchInTitle: Boolean = _sortAndKeyword.value.searchInTitle,
        searchInContent: Boolean = _sortAndKeyword.value.searchInContent,
        likeChange: Boolean? = null
    ) {
        val prevLike = _sortAndKeyword.value.likeChange
        val newLike = if (likeChange != null) !prevLike else prevLike
        val newValue = SortAndKeyword(sort, keyword, searchInTitle, searchInContent, newLike)
        _sortAndKeyword.value = newValue
    }

    fun loadS3Image(view: ImageView, key: String) = view.loadS3Image(key, s3Client)
}
