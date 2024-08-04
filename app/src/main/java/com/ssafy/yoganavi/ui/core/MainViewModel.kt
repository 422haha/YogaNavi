package com.ssafy.yoganavi.ui.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.dataStore.DataStoreRepository
import com.ssafy.yoganavi.data.source.dto.home.EmptyData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _mainEvent: MutableSharedFlow<MainEvent> = MutableSharedFlow()
    val mainEvent: SharedFlow<MainEvent> = _mainEvent.asSharedFlow()

    private val _emptyEvent: MutableSharedFlow<EmptyData> = MutableSharedFlow()
    val emptyEvent: SharedFlow<EmptyData> = _emptyEvent.asSharedFlow()

    fun setMainEvent(
        isBottomNavigationVisible: Boolean,
        title: String,
        canGoBack: Boolean,
        menuItem: String? = null,
        menuListener: (() -> Unit)? = null
    ) = viewModelScope.launch {
        val event = MainEvent(isBottomNavigationVisible, title, canGoBack, menuItem, menuListener)
        _mainEvent.emit(event)
    }

    fun clearToken() = viewModelScope.launch {
        dataStoreRepository.clearToken()
    }

    fun setEmptyView(emptyData: EmptyData) = viewModelScope.launch {
        _emptyEvent.emit(emptyData)
    }
}