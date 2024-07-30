package com.ssafy.yoganavi.ui.utils

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

class PermissionHelper(
    private val fragment: Fragment,
    private val permissions: Array<String>
) {
    private val requestPermissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            checkResult(it)
        }

    private val resultLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (!checkPermissions()) makeDialog()
        }

    fun launchPermission() = requestPermissionLauncher.launch(permissions)

    private fun checkResult(result: Map<String, Boolean>) {
        if (result.containsValue(false)) makeDialog()
    }

    private fun makeDialog() {
        AlertDialog.Builder(fragment.requireContext())
            .setTitle("권한이 필요합니다.")
            .setMessage("설정으로 이동합니다.")
            .setNegativeButton("취소") { dialog, _ -> finish(dialog) }
            .setPositiveButton("확인") { dialog, _ -> moveSettings(dialog) }
            .show()
    }

    private fun finish(dialog: DialogInterface) {
        Toast.makeText(fragment.requireContext(), "권한을 허락하지 않으면 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
        dialog.cancel()
        fragment.requireActivity().finish()
    }

    private fun moveSettings(dialog: DialogInterface) {
        val uri = Uri.parse("package:${fragment.requireContext().packageName}")
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri)
        resultLauncher.launch(intent)
        dialog.cancel()
    }

    private fun checkPermissions(): Boolean {
        return permissions.all {
            ActivityCompat.checkSelfPermission(fragment.requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }
}
