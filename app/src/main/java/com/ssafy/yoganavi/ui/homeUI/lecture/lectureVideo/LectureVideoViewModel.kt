package com.ssafy.yoganavi.ui.homeUI.lecture.lectureVideo

import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LectureVideoViewModel @Inject constructor() : ViewModel() {

    fun inferImage(image: ImageProxy) {
        Timber.d("IMAGE: ${image.imageInfo}")
        image.close()
    }

}
