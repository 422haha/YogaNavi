package com.ssafy.yoganavi.ui.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _mainEvent: MutableSharedFlow<MainEvent> = MutableSharedFlow()
    val mainEvent: SharedFlow<MainEvent> = _mainEvent.asSharedFlow()

    fun setMainEvent(event: MainEvent) = viewModelScope.launch {
        _mainEvent.emit(event)
    }
}
