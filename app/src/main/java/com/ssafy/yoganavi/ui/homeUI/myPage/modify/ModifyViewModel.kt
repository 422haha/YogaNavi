package com.ssafy.yoganavi.ui.homeUI.myPage.modify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.services.s3.AmazonS3Client
import com.ssafy.yoganavi.data.repository.InfoRepository
import com.ssafy.yoganavi.data.source.mypage.Profile
import com.ssafy.yoganavi.ui.utils.BUCKET_NAME
import com.ssafy.yoganavi.ui.utils.LOGO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ModifyViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
    private val s3Client: AmazonS3Client
) : ViewModel() {

    private val _hashtagList: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val hashtagList: StateFlow<List<String>> = _hashtagList.asStateFlow()
    var profile = Profile()

    fun getProfile(bindData: suspend (Profile) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getProfile() }
            .onSuccess { it.data?.let { data -> bindData(data) } }
            .onFailure { it.printStackTrace() }
    }

    fun addHashTag(hashTag: String) = viewModelScope.launch(Dispatchers.IO) {
        val newList = hashtagList.value.toMutableList()
        newList.add(hashTag)
        _hashtagList.emit(newList)
    }

    fun deleteHashTag(index: Int) = viewModelScope.launch(Dispatchers.IO) {
        val newList = hashtagList.value.toMutableList()
        newList.removeAt(index)
        _hashtagList.emit(newList)
    }

    fun modifyProfile(nickname: String, password: String) = viewModelScope.launch(Dispatchers.IO) {
        profile = profile.copy(
            nickname = nickname,
            password = password,
            hashTags = hashtagList.value
        )

        // TODO 서버 전송
    }

    fun setThumbnail(path: String) = viewModelScope.launch(Dispatchers.IO) {
        val logoKey = "$LOGO/${UUID.randomUUID()}"
        val imageUrl = s3Client.getUrl(BUCKET_NAME, logoKey)

        profile = profile.copy(
            imageUrl = imageUrl.toString(),
            logoPath = path,
            logoKey = logoKey
        )
    }
}
