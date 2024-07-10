package com.example.notes.presentation.listnotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.data.model.Note
import com.example.notes.databinding.FragmentListNoteBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ListNoteFragment : Fragment(){
    private var _binding: FragmentListNoteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ListNodeViewModel by viewModels()
    private lateinit var notesAdapter: NotesAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListNoteBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        notesAdapter = NotesAdapter{ note ->
            val bundle = Bundle().apply { putSerializable("note", note) }
            findNavController().navigate(resId = R.id.action_listNoteFragment_to_updateNoteFragment, args = bundle)
        }
        binding.recyclerView.adapter = notesAdapter
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(resId = R.id.action_listNoteFragment_to_addNoteFragment2)
        }
        handleSwipeToDelete()
        observeAllNotes()
    }

    private fun handleSwipeToDelete(){
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                notesAdapter.notes?.get(position)?.let { note: Note ->
                    viewModel.deleteNote(note)
                    view?.let {
                        Snackbar.make(it, "Successfully deleted article", Snackbar.LENGTH_LONG).apply {
                            setAction("Undo") {
                                viewModel.insertNote(note)
                            }
                            show()
                        }
                    }
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerView)
        }
    }

    private fun observeAllNotes(){
        lifecycleScope.launch {
            viewModel.allNotes.flowWithLifecycle(lifecycle, minActiveState = Lifecycle.State.STARTED)
                .collect{
                    notesAdapter.notes = it
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}