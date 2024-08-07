package com.ssafy.yoganavi.ui.homeUI.schedule.home

import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.services.s3.AmazonS3Client
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.source.dto.home.HomeData
import com.ssafy.yoganavi.ui.utils.loadS3Image
import com.ssafy.yoganavi.ui.utils.loadS3ImageSequentially
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
    private val s3Client: AmazonS3Client
) : ViewModel() {
    private val _homeList = MutableStateFlow<List<HomeData>>(emptyList())
    val homeList = _homeList.asStateFlow()

    fun getHomeList() = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getHomeList() }
            .onSuccess { _homeList.emit(it.data.toMutableList()) }
            .onFailure { it.printStackTrace() }
    }

    fun loadS3Image(view: ImageView, key: String) = view.loadS3Image(key, s3Client)

    fun loadS3ImageSequentially(
        view: ImageView,
        smallKey: String,
        largeKey: String
    ) = view.loadS3ImageSequentially(smallKey, largeKey, s3Client)

}
