package com.ssafy.yoganavi.data.repository.ai

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.ssafy.yoganavi.data.source.ai.PoseDataSource
import com.ssafy.yoganavi.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PoseRepositoryImpl @Inject constructor(
    private val poseDataSource: PoseDataSource,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : PoseRepository {

    override suspend fun infer(image: ImageProxy, width: Int, height: Int): List<FloatArray> {
        val result = withContext(defaultDispatcher) { poseDataSource.infer(image, width, height) }
        return removeBox(result)
    }

    override suspend fun infer(bitmap: Bitmap, width: Int, height: Int): List<FloatArray> {
        val result = withContext(defaultDispatcher) { poseDataSource.infer(bitmap, width, height) }
        return removeBox(result)
    }

    private fun removeBox(result: List<FloatArray>): List<FloatArray> {
        return result.map { keyPoints ->
            keyPoints.copyOfRange(5, keyPoints.size)
        }
    }
}