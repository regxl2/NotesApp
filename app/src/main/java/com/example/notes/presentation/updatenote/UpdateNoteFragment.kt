package com.example.notes.presentation.updatenote

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notes.Notification
import com.example.notes.data.model.Note
import com.example.notes.databinding.FragmentUpdateNoteBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UpdateNoteFragment : Fragment() {
    private var _binding: FragmentUpdateNoteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UpdateNoteViewModel by viewModels()
    private val args: UpdateNoteFragmentArgs by navArgs()
    private lateinit var notification: Notification

    override fun onAttach(context: Context) {
        super.onAttach(context)
        notification = context as Notification
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateNoteBinding.inflate(layoutInflater, container, false)
        val note = args.note
        binding.titleEditText.setText(note.title)
        binding.descriptionEditText.setText(note.description)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListenerOnUpdateButton()
    }

    private fun setOnClickListenerOnUpdateButton(){
        binding.apply {
            addButton.setOnClickListener {
                val title = titleEditText.text.toString()
                if(title.isBlank()){
                    errorTextView.visibility = View.VISIBLE
                }
                else{
                    val id = args.note.id
                    val description = descriptionEditText.text.toString()
                    viewModel.updateNote(Note(id = id, title = title, description = description))
                    notification.showSnackBar("Note is updated successfully")
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