package com.ssafy.yoganavi.ui.core

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.forEach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.ActivityMainBinding
import com.ssafy.yoganavi.ui.utils.AuthManager
import com.ssafy.yoganavi.ui.utils.PermissionHandler
import com.ssafy.yoganavi.ui.utils.SESSION_END
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var authManager: AuthManager
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted)
                Snackbar.make(
                    binding.root,
                    "실시간 강의 알림은 권한이 필요합니다.\n설정 화면에서 알림 권한을 허용해주세요.",
                    Snackbar.LENGTH_SHORT
                ).show()
        }

    private val permissionHandler: PermissionHandler by lazy {
        PermissionHandler(
            this,
            requestPermissionLauncher
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tb)

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
        lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
            collectMainEvent()
            collectAuthEvent()
            collectEmptyEvent()
        }
    }

    private fun CoroutineScope.collectMainEvent() = launch {
        viewModel.mainEvent.collectLatest { event ->
            setBottomNavigationVisible(event.isBottomNavigationVisible)
            setBottomNavClickable(event.isBottomNavigationVisible)
            setTitle(event.title)
            setMenuItem(event.menuItem, event.menuListener)
            checkGoBack(event.canGoBack)
        }
    }

    private fun CoroutineScope.collectAuthEvent() = launch {
        authManager.authEvent.collect {
            Toast.makeText(this@MainActivity, SESSION_END, Toast.LENGTH_SHORT).show()
            viewModel.clearToken()
            moveToLogin()
        }
    }

    private fun CoroutineScope.collectEmptyEvent() = launch {
        viewModel.emptyEvent.collectLatest {
            if (it.isEmpty) {
                delay(500)
                binding.clEmpty.tvEmpty.text = it.emptyName
                binding.clEmpty.root.visibility = View.VISIBLE
            } else {
                binding.clEmpty.root.visibility = View.GONE
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

    fun setMenuItemAvailable(isAvailable: Boolean){
        binding.tvMenu.isEnabled = isAvailable
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

    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun setBottomNavClickable(canClick: Boolean) {
        binding.bnv.menu.forEach { item -> item.isEnabled = canClick }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val currentFocus = currentFocus ?: return super.dispatchTouchEvent(ev)
        val rect = Rect()
        currentFocus.getGlobalVisibleRect(rect)
        val x = ev.x.toInt()
        val y = ev.y.toInt()
        if (rect.contains(x, y)) return super.dispatchTouchEvent(ev)

        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.let { imm.hideSoftInputFromWindow(currentFocus.windowToken, 0) }

        currentFocus.clearFocus()
        return super.dispatchTouchEvent(ev)
    }
}
