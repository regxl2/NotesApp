package com.example.notes.data.repository

import kotlinx.coroutines.flow.Flow

interface SettingRepository {
    companion object{
        const val IS_LOGIN = "is_user_login"
    }
    fun readLoginStatus(): Flow<Boolean>
    suspend fun updateLoginStatus(status: Boolean)
}