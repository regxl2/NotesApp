package com.example.notes.presentation.googleauth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.repository.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthFragmentViewModel@Inject constructor(private val settingRepository: SettingRepository): ViewModel() {
    fun setIsLoginStatus(status: Boolean){
        viewModelScope.launch(Dispatchers.IO){
            settingRepository.updateLoginStatus(status)
        }
    }
}