package com.example.notes.presentation.addnote

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notes.Notification
import com.example.notes.databinding.FragmentAddNoteBinding
import com.example.notes.data.model.Note
import com.example.notes.data.model.fakeNotes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNoteFragment : Fragment() {
    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var notification: Notification
    private val viewModel: AddNoteViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        notification = context as Notification
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListenerOnAddButton()
    }

    private fun setOnClickListenerOnAddButton(){
        binding.apply {
            addButton.setOnClickListener {
                val title = titleEditText.text.toString()
                if(title.isBlank()){
                    errorTextView.visibility = View.VISIBLE
                }
                else{
                    val description = descriptionEditText.text.toString()
                    viewModel.addNote(Note(title = title, description = description))
                    notification.showSnackBar("Note is saved successfully")
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}