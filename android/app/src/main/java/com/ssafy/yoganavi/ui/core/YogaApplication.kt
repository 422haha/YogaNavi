package com.ssafy.yoganavi.ui.core

import android.app.Application
import com.ssafy.yoganavi.ui.utils.YOGA
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

@HiltAndroidApp
class YogaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        CoroutineScope(Dispatchers.IO).launch { clearYogaCaches() }
    }

    private fun clearYogaCaches() = runCatching {
        deleteYogaDirectory(cacheDir)
        externalCacheDir?.let { deleteYogaDirectory(it) }
    }.onFailure {
        it.printStackTrace()
    }

    private fun deleteYogaDirectory(dir: File) {
        if (!dir.exists() || !dir.isDirectory) return

        dir.listFiles()?.forEach { file ->
            if (file.isDirectory && file.name.startsWith(YOGA)) {
                deleteDirectoryContents(file)
            }
        }
    }

    private fun deleteDirectoryContents(dir: File) {
        if (!dir.exists() || !dir.isDirectory) return

        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                deleteDirectoryContents(file)
                file.delete()
            } else {
                file.delete()
            }
        }
    }
}