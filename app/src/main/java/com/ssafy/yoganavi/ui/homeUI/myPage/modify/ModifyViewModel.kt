package com.ssafy.yoganavi.ui.homeUI.myPage.modify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.ssafy.yoganavi.data.repository.InfoRepository
import com.ssafy.yoganavi.data.repository.response.DetailResponse
import com.ssafy.yoganavi.data.source.dto.mypage.Profile
import com.ssafy.yoganavi.ui.utils.BUCKET_NAME
import com.ssafy.yoganavi.ui.utils.LOGO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ModifyViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
    private val transferUtility: TransferUtility,
    private val s3Client: AmazonS3Client
) : ViewModel() {

    private val _hashtagList: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val hashtagList: StateFlow<List<String>> = _hashtagList.asStateFlow()
    var profile = Profile()

    fun getProfile(bindData: suspend (Profile) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            infoRepository.getProfile()
        }.onSuccess {
            it.data?.let { data ->
                profile = profile.copy(
                    nickname = data.nickname,
                    imageUrl = data.imageUrl,
                    teacher = data.teacher
                )
                _hashtagList.emit(data.hashTags)
                bindData(data)
            }
        }.onFailure {
            it.printStackTrace()
        }
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

    fun modifyProfile(
        nickname: String,
        password: String,
        isModified: (DetailResponse<Profile>) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        profile = profile.copy(
            nickname = nickname,
            password = password,
            hashTags = hashtagList.value
        )

        if (profile.logoPath.isNotBlank()) {
            val thumbnailFile = File(profile.logoPath)
            val metadata = ObjectMetadata().apply { contentType = "image/webp" }
            transferUtility.upload(
                BUCKET_NAME,
                profile.logoKey,
                thumbnailFile,
                metadata
            )
        } else {
            val url = profile.imageUrl?.substringBefore("?")
            profile = profile.copy(
                imageUrl = url
            )
        }

        runCatching { infoRepository.updateProfile(profile) }
            .onSuccess { isModified(it) }
            .onFailure { it.printStackTrace() }
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
