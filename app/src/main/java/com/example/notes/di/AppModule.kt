package com.example.notes.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.room.Room
import com.example.notes.data.database.NoteDao
import com.example.notes.data.database.NoteDatabase
import com.example.notes.data.repository.NoteRepository
import com.example.notes.data.repository.NoteRepositoryImpl
import com.example.notes.data.repository.SettingRepository
import com.example.notes.data.repository.SettingRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @ApiKey
    @Provides
    fun getApiKey(@ApplicationContext context: Context): String{
        val applicationInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        return applicationInfo.metaData.getString("CLIENT_ID", "")
    }

    @Singleton
    @Provides
    fun getDatabase(@ApplicationContext context: Context): NoteDatabase {
        return Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            "note-database"
        )
            .build()
    }

    @Singleton
    @Provides
    fun getNoteDao(noteDatabase: NoteDatabase): NoteDao {
        return noteDatabase.noteDao()
    }

    @Singleton
    @Provides
    fun getNoteRepository(noteDao: NoteDao): NoteRepository {
        return NoteRepositoryImpl(noteDao)
    }

    @Singleton
    @Provides
    fun getLoginPreferenceKey(): Preferences.Key<Boolean> {
        return booleanPreferencesKey(SettingRepository.IS_LOGIN)
    }


    @Singleton
    @Provides
    fun getSettingRepository(
        @ApplicationContext context: Context,
        loginPreferencesKey: Preferences.Key<Boolean>
    ): SettingRepository {
        return SettingRepositoryImpl(context = context, loginPreferencesKey = loginPreferencesKey)
    }
}