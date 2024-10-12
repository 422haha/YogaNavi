package com.ssafy.yoganavi.ui.core

class MainEvent(
    val isBottomNavigationVisible: Boolean,
    val title: String,
    val canGoBack: Boolean,
    val menuItem: String? = null,
    val menuListener: (() -> Unit)? = null
)
