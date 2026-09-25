package com.example.book_slide.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.book_slide.DataClasses.Users.UsersDatabase
import com.example.book_slide.DataClasses.Users.UsersRepository
import com.example.book_slide.DataClasses.Users.UsersViewModel
import com.example.book_slide.DataClasses.Users.UsersViewModelFactory
import com.example.book_slide.R
import com.example.book_slide.databinding.FragmentLogInBinding
import com.example.book_slide.util.ColorBlindnessManager
import com.example.book_slide.util.ColorBlindnessMode
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LogInFragment : Fragment() {

    private val database by lazy {
        UsersDatabase().getUserDb(requireContext())
    }

    private val repository by lazy {
        UsersRepository(database.UsersDao())
    }

    private val viewModel: UsersViewModel by viewModels {
        UsersViewModelFactory(repository)
    }

    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    private val webClientId = "846250977181-78snb8np7pq791pqot0jobbb4dk8nj1v.apps.googleusercontent.com"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                ColorBlindnessManager.currentMode.collect { mode ->
                    applyColorBlindnessMode(mode)
                }
            }
        }

        // Login Híbrido: Firebase Auth primero, fallback a Room si falla por red
        binding.button.setOnClickListener {
            val email = binding.editTextText.text.toString().trim()
            val password = binding.editTextTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Exito con red (Firebase) -> Sincronizamos estado en Room si es necesario
                        viewModel.login(email, password) { }
                        Toast.makeText(requireContext(), "Sesión iniciada (Online)", Toast.LENGTH_SHORT).show()
                        navegarAHome()
                    } else {
                        // Fallo en Firebase Auth -> Intentamos la verificación offline con Room
                        viewModel.login(email, password) { successLocal ->
                            if (successLocal) {
                                Toast.makeText(requireContext(), "Sesión iniciada (Modo Offline)", Toast.LENGTH_SHORT).show()
                                navegarAHome()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Error: Credenciales incorrectas o sin conexión",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
        }

        binding.button2.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.imageButton4.setOnClickListener {
            iniciarGoogleSSO()
        }
    }

    private fun navegarAHome() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomeFragment())
            .commit()
    }

    private fun iniciarGoogleSSO() {
        val credentialManager = CredentialManager.create(requireContext())

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = requireContext()
                )
                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken

                    viewModel.loginWithGoogleToken(idToken) { success ->
                        if (success) {
                            Toast.makeText(requireContext(), "Sesión iniciada con Google", Toast.LENGTH_SHORT).show()
                            navegarAHome()
                        } else {
                            Toast.makeText(requireContext(), "Error al autenticar con Firebase", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Inicio de sesión cancelado o fallido: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun applyColorBlindnessMode(mode: ColorBlindnessMode) {
        val colorStateList = ColorStateList.valueOf(mode.primaryColorHex)

        binding.textView4.setTextColor(mode.primaryColorHex)

        binding.editTextText.setTextColor(Color.WHITE)
        binding.editTextText.setHintTextColor(Color.LTGRAY)
        binding.editTextTextPassword.setTextColor(Color.WHITE)
        binding.editTextTextPassword.setHintTextColor(Color.LTGRAY)

        binding.editTextText.background?.setTint(mode.primaryColorHex)
        binding.editTextTextPassword.background?.setTint(mode.primaryColorHex)

        binding.button.backgroundTintList = colorStateList
        binding.button.setTextColor(Color.BLACK)

        binding.button2.background?.setTint(mode.primaryColorHex)
        binding.button2.setTextColor(Color.BLACK)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String = "", param2: String = "") = LogInFragment()
    }
}