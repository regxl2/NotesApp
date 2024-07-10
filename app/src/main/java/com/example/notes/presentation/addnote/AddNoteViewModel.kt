package com.example.notes.presentation.addnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.repository.NoteRepository
import com.example.notes.data.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel@Inject constructor(private val noteRepository: NoteRepository): ViewModel(){
    fun addNote(note: Note){
        viewModelScope.launch {
            noteRepository.insertNote(note)
        }
    }
}