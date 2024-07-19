package com.example.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.repository.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(settingRepository: SettingRepository) :
    ViewModel() {
    private var _showSplashScreen = MutableStateFlow(false)
    val showSplashScreen = _showSplashScreen.asStateFlow()

    private var _isLogin = MutableStateFlow(false)
    val isLogin = _isLogin.asStateFlow()

    init {
        settingRepository.readLoginStatus()
            .onEach { value ->
                delay(300)
                _isLogin.value = value
                _showSplashScreen.value = false
            }
            .launchIn(viewModelScope)
    }
}