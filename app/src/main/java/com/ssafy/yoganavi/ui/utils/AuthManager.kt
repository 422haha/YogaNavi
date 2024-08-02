package com.ssafy.yoganavi.ui.utils

import com.ssafy.yoganavi.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private val _authEvent = MutableSharedFlow<String>()
    val authEvent: SharedFlow<String> = _authEvent.asSharedFlow()

    fun authError(value: String) = CoroutineScope(ioDispatcher).launch {
        _authEvent.emit(value)
    }
}