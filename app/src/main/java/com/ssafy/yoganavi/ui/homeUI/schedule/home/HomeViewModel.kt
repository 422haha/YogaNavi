package com.ssafy.yoganavi.ui.homeUI.schedule.home

import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.amazonaws.services.s3.AmazonS3Client
import com.ssafy.yoganavi.data.source.dto.home.HomeData
import com.ssafy.yoganavi.data.repository.home.HomePagingRepo
import com.ssafy.yoganavi.ui.utils.loadS3Image
import com.ssafy.yoganavi.ui.utils.loadS3ImageSequentially
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homePagingRepo: HomePagingRepo,
    private val s3Client: AmazonS3Client
) : ViewModel() {

    var homeList2: Flow<PagingData<HomeData>> =
        homePagingRepo.getHomeList()
            .cachedIn(viewModelScope)

    fun loadS3Image(view: ImageView, key: String) = view.loadS3Image(key, s3Client)

    fun loadS3ImageSequentially(
        view: ImageView,
        smallKey: String,
        largeKey: String
    ) = view.loadS3ImageSequentially(smallKey, largeKey, s3Client)
}