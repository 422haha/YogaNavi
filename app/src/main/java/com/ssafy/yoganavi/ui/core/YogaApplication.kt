package com.ssafy.yoganavi.ui.core

import android.app.Application
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
        CoroutineScope(Dispatchers.IO).launch { clearAllCaches() }
    }

    private fun clearAllCaches() = runCatching {
        deleteDirectory(cacheDir)
        externalCacheDir?.let { deleteDirectory(it) }
        codeCacheDir?.let { deleteDirectory(it) }
    }.onFailure {
        it.printStackTrace()
    }

    private fun deleteDirectory(dir: File): Boolean {
        if (!dir.exists()) return dir.delete()

        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) deleteDirectory(file)
            else file.delete()
        }
        return dir.delete()
    }
}