package com.ssafy.yoganavi.ui.core

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.ActivityMainBinding
import com.ssafy.yoganavi.ui.utils.PermissionHandler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if(!isGranted)
                Snackbar.make(binding.root, "실시간 강의 알림은 권한이 필요합니다.\n설정 화면에서 알림 권한을 허용해주세요.", Snackbar.LENGTH_SHORT).show()
        }

    private val permissionHandler: PermissionHandler by lazy { PermissionHandler(this, requestPermissionLauncher) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tb)

        LiveFcmService().getFirebaseToken()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            permissionHandler.branchPermission(Manifest.permission.POST_NOTIFICATIONS, "알림")

        initGestureBarColor()
        connectBottomNav()
        initCollect()
    }

    private fun connectBottomNav() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fl) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bnv.setupWithNavController(navController)
    }

    private fun initGestureBarColor() {
        window.apply {
            navigationBarColor = resources.getColor(R.color.bottomnav, null)
            statusBarColor = Color.WHITE
        }
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    private fun initCollect() = lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.mainEvent.collectLatest { event ->
                setBottomNavigationVisible(event.isBottomNavigationVisible)
                setTitle(event.title)
                setMenuItem(event.menuItem, event.menuListener)
                checkGoBack(event.canGoBack)
            }
        }
    }

    private fun setMenuItem(menuItem: String?, menuListener: (() -> Unit)?) {
        if (menuItem != null && menuListener != null) {
            binding.tvMenu.text = menuItem
            binding.tvMenu.setOnClickListener { menuListener() }
        } else {
            binding.tvMenu.text = null
        }
    }

    private fun setBottomNavigationVisible(isVisible: Boolean) {
        if (isVisible) binding.bnv.visibility = View.VISIBLE
        else binding.bnv.visibility = View.GONE
    }

    private fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

    private fun checkGoBack(canGoBack: Boolean) {
        if (canGoBack) {
            binding.ivGoBack.visibility = View.VISIBLE
            binding.ivGoBack.setOnClickListener {
                findNavController(R.id.fl).popBackStack()
            }
        } else {
            binding.ivGoBack.visibility = View.GONE
        }
    }
}
