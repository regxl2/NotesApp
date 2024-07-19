package com.example.notes.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject


val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "setting")

class SettingRepositoryImpl @Inject constructor(private val context: Context, private val loginPreferencesKey: Preferences.Key<Boolean>): SettingRepository {
    override fun readLoginStatus(): Flow<Boolean> {
        return context.datastore.data
            .catch {
                if(it is IOException){
                    emit(emptyPreferences())
                }
                else{
                    throw it
                }
            }
            .map { preferences ->
                preferences[loginPreferencesKey] ?: false
            }
    }

    override suspend fun updateLoginStatus(status: Boolean) {
        context.datastore.edit { settings ->
            settings[loginPreferencesKey] = status
        }
    }
}