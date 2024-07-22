package com.ssafy.yoganavi.ui.core

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
