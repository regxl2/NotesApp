package com.example.notes.di

import android.content.Context
import androidx.room.Room
import com.example.notes.data.database.NoteDao
import com.example.notes.data.database.NoteDatabase
import com.example.notes.data.repository.NoteRepository
import com.example.notes.data.repository.NoteRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun getDatabase(@ApplicationContext context: Context): NoteDatabase {
        return Room.databaseBuilder(context,
            NoteDatabase::class.java,
            "note-database")
            .build()
    }

    @Singleton
    @Provides
    fun getNoteDao(noteDatabase: NoteDatabase): NoteDao{
        return noteDatabase.noteDao()
    }

    @Singleton
    @Provides
    fun getNoteRepository(noteDao: NoteDao): NoteRepository{
        return NoteRepositoryImpl(noteDao)
    }
}