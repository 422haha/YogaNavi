package com.ssafy.yoganavi.ui.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _tokenEvent = MutableSharedFlow<String>()
    val tokenEvent: SharedFlow<String> = _tokenEvent.asSharedFlow()

    fun getToken() = viewModelScope.launch(Dispatchers.IO) {
        val token = dataStoreRepository.token.firstOrNull()
        token?.let { _tokenEvent.emit(it) }
    }
}