package com.ssafy.yoganavi.ui.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ssafy.yoganavi.ui.core.MainActivity

class PermissionHandler(
    private val context: Context,
    private val requestPermissionLauncher: ActivityResultLauncher<String>) {

    fun branchPermission(permission: String, msg: String) {
        when {
            // 이미 권한이 존재
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> { }
            // 권한이 최초 거절
            ActivityCompat.shouldShowRequestPermissionRationale(
                context as MainActivity, permission
            ) -> { requestPermissionLauncher.launch(permission) }
            // 권한을 최초 확인
            else -> { requestPermissionLauncher.launch(permission) }
        }
    }
}