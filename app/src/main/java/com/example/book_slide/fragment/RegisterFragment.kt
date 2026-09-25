package com.example.book_slide.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.book_slide.DataClasses.Users.Users
import com.example.book_slide.DataClasses.Users.UsersDatabase
import com.example.book_slide.DataClasses.Users.UsersRepository
import com.example.book_slide.DataClasses.Users.UsersViewModel
import com.example.book_slide.DataClasses.Users.UsersViewModelFactory
import com.example.book_slide.databinding.FragmentRegisterBinding
import com.example.book_slide.util.ColorBlindnessManager
import com.example.book_slide.util.ColorBlindnessMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val database by lazy {
        UsersDatabase().getUserDb(requireContext())
    }

    private val repository by lazy {
        UsersRepository(database.UsersDao())
    }

    private val viewModel: UsersViewModel by viewModels {
        UsersViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
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

        binding.btnRegister.setOnClickListener {
            val username = binding.etRegisterUsername.text.toString().trim()
            val email = binding.etRegisterEmail.text.toString().trim()
            val password = binding.etRegisterPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                saveUserHybrid(username, email, password)
            } else {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserHybrid(username: String, email: String, password: String) {
        // Firebase Auth exige un mínimo de 6 caracteres
        if (password.length < 6) {
            Toast.makeText(
                requireContext(),
                "La contraseña debe tener al menos 6 caracteres",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Deshabilitar botón durante el proceso
        binding.btnRegister.isEnabled = false

        // 1. Intentar crear la cuenta primero en Firebase Auth
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // Volver a habilitar el botón
                if (isAdded) {
                    binding.btnRegister.isEnabled = true
                }

                if (task.isSuccessful) {
                    val firebaseUser = FirebaseAuth.getInstance().currentUser

                    // Asignar el nombre de usuario al perfil de Firebase si se ingresó
                    if (username.isNotEmpty()) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()
                        firebaseUser?.updateProfile(profileUpdates)
                    }

                    // 2. Guardar en Room SOLO si la autenticación con Firebase fue exitosa
                    val localUser = Users(
                        email = email,
                        password = password
                    )
                    viewModel.registerUser(localUser)

                    Toast.makeText(
                        requireContext(),
                        "Registro exitoso en Firebase y Room",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Regresar a la pantalla anterior solo tras un registro exitoso
                    parentFragmentManager.popBackStack()

                } else {
                    // Muestra y registra la causa exacta del fallo de Firebase
                    val exception = task.exception
                    val errorMessage = exception?.localizedMessage ?: "Error al registrar en Firebase"

                    Log.e("RegisterFragment", "Error en Firebase Auth", exception)
                    Toast.makeText(
                        requireContext(),
                        "Error Firebase: $errorMessage",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun applyColorBlindnessMode(mode: ColorBlindnessMode) {
        val colorStateList = ColorStateList.valueOf(mode.primaryColorHex)

        binding.registerTitle.setTextColor(mode.primaryColorHex)

        binding.etRegisterUsername.setTextColor(Color.WHITE)
        binding.etRegisterUsername.setHintTextColor(Color.LTGRAY)
        binding.etRegisterUsername.background?.setTint(mode.primaryColorHex)

        binding.etRegisterEmail.setTextColor(Color.WHITE)
        binding.etRegisterEmail.setHintTextColor(Color.LTGRAY)
        binding.etRegisterEmail.background?.setTint(mode.primaryColorHex)

        binding.etRegisterPassword.setTextColor(Color.WHITE)
        binding.etRegisterPassword.setHintTextColor(Color.LTGRAY)
        binding.etRegisterPassword.background?.setTint(mode.primaryColorHex)

        binding.btnRegister.backgroundTintList = colorStateList
        binding.btnRegister.setTextColor(Color.BLACK)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = RegisterFragment()
    }
}