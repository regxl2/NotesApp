package com.example.notes.presentation.googleauth

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.databinding.FragmentAuthBinding
import com.example.notes.di.ApiKey
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject


@AndroidEntryPoint
class AuthFragment : Fragment() {
    @Inject
    @ApiKey
    lateinit var clientId: String
    private var _binding: FragmentAuthBinding? = null
    private lateinit var credentialManager: CredentialManager
    private val binding:FragmentAuthBinding get() = _binding!!
    private val viewModel: AuthFragmentViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        credentialManager = CredentialManager.create(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        setMenuVisibility(true)
        _binding = FragmentAuthBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.googleLogin.setOnClickListener{
            val googleIdOption = getGoogleIdOption()
            val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
            lifecycleScope.launch {
                try {
                    val result = credentialManager.getCredential(requireActivity(), request)
                    handleSignIn(result)
                }
                catch (e: GetCredentialException){
                    e.message?.let { showErrorSnackBar(it) }?: showErrorSnackBar()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getGoogleIdOption(): GetGoogleIdOption{
        val nonce = getNonce()
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(true)
            .setNonce(nonce)
        .build()
    }

    private fun getNonce(): String{
        val rawNonce =  UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold(""){str, it -> str + "%02x".format(it)}
        return hashedNonce
    }


    fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        viewModel.setIsLoginStatus(true)
                        findNavController().navigate(R.id.action_authFragment_to_listNoteFragment)
                        findNavController().graph.setStartDestination(R.id.listNoteFragment)
                    } catch (e: GoogleIdTokenParsingException) {
                        showErrorSnackBar("Received an invalid google id token response")
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    showErrorSnackBar("Unexpected type of credential")
                    Log.e(TAG, "Unexpected type of credential")
                }
            }
            else -> {
                showErrorSnackBar("Unexpected type of credential")
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }


    private fun showErrorSnackBar(msg: String = "Something went wrong"){
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "AuthFragment"
    }
}