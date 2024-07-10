package com.example.notes.presentation.listnotes

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.databinding.NotesItemBinding
import com.example.notes.data.model.Note

class NotesAdapter(private val onClickNote: (note: Note) -> Unit) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {


    var notes: List<Note>? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class NoteViewHolder(val binding: NotesItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.cardText1.text = note.title
            binding.cardText2.text = note.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NotesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return notes?.size ?: 0
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes?.get(position) ?: return
        holder.binding.card.setOnClickListener {
            onClickNote(note)
        }
        holder.bind(note)
    }
}