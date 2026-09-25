package com.example.book_slide.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.example.book_slide.databinding.FragmentProfileBinding
import com.example.book_slide.util.ColorBlindnessManager
import com.example.book_slide.util.ColorBlindnessMode
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
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
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Escuchar cambios de color para daltonismo
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                ColorBlindnessManager.currentMode.collect { mode ->
                    applyColorBlindnessMode(mode)
                }
            }
        }

        // Cargar datos de la sesión actual (Firebase o Local)
        cargarDatosUsuario()

        // Evento de Cerrar Sesión
        binding.btnLogout.setOnClickListener {
            // 1. Cerrar sesión en Firebase Auth
            FirebaseAuth.getInstance().signOut()

            Toast.makeText(
                requireContext(),
                "Sesión cerrada correctamente",
                Toast.LENGTH_SHORT
            ).show()

            // 2. Redirigir al LogInFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LogInFragment())
                .commit()
        }
    }

    private fun cargarDatosUsuario() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // Caso 1: Sesión activa con Firebase Auth
            val email = currentUser.email ?: "Sin correo"
            binding.tvProfileEmail.text = email
            binding.tvProfileUsername.text = currentUser.displayName
                ?: if (email.contains("@")) email.substringBefore("@") else "Usuario"
        } else {
            // Caso 2: Sesión Local u Offline (recupera el correo pasado por argumentos)
            val userEmail = arguments?.getString("USER_EMAIL")

            if (!userEmail.isNullOrEmpty() && userEmail != "Invitado") {
                binding.tvProfileEmail.text = userEmail
                binding.tvProfileUsername.text = if (userEmail.contains("@")) {
                    userEmail.substringBefore("@")
                } else {
                    userEmail
                }
            } else {
                binding.tvProfileEmail.text = "Sin sesión activa"
                binding.tvProfileUsername.text = "Invitado"
            }
        }
    }

    private fun applyColorBlindnessMode(mode: ColorBlindnessMode) {
        val colorStateList = ColorStateList.valueOf(mode.primaryColorHex)

        // Título con color primario según modo
        binding.tvProfileTitle.setTextColor(mode.primaryColorHex)

        // Botón Cerrar Sesión adaptado al modo
        binding.btnLogout.backgroundTintList = colorStateList
        binding.btnLogout.setTextColor(Color.BLACK)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(email: String = ""): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle().apply {
                putString("USER_EMAIL", email)
            }
            fragment.arguments = args
            return fragment
        }
    }
}