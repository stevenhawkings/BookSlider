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
import com.example.book_slide.databinding.FragmentLogInBinding
import com.example.book_slide.util.ColorBlindnessManager
import com.example.book_slide.util.ColorBlindnessMode
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

        binding.button.setOnClickListener {
            val email = binding.editTextText.text.toString()
            val password = binding.editTextTextPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, password) { success ->
                if (success) {
                    Toast.makeText(
                        requireContext(),
                        "Sesión iniciada",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error: comprueba el usuario y contraseña",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.button2.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    RegisterFragment()
                )
                .addToBackStack(null)
                .commit()
        }
    }

    private fun applyColorBlindnessMode(mode: ColorBlindnessMode) {
        val colorStateList = ColorStateList.valueOf(mode.primaryColorHex)

        binding.textView4.setTextColor(mode.primaryColorHex)

        // Textos del formulario con alto contraste (Texto blanco, hint gris claro)
        binding.editTextText.setTextColor(Color.WHITE)
        binding.editTextText.setHintTextColor(Color.LTGRAY)
        binding.editTextTextPassword.setTextColor(Color.WHITE)
        binding.editTextTextPassword.setHintTextColor(Color.LTGRAY)

        // Borde de los campos de texto adaptados al modo de daltonismo
        binding.editTextText.background?.setTint(mode.primaryColorHex)
        binding.editTextTextPassword.background?.setTint(mode.primaryColorHex)

        // Botón Iniciar Sesión (Fondo color primario, texto NEGRO para alta legibilidad)
        binding.button.backgroundTintList = colorStateList
        binding.button.setTextColor(Color.BLACK)

        // Botón Registro (Fondo color primario, texto NEGRO para alta legibilidad)
        binding.button2.background?.setTint(mode.primaryColorHex)
        binding.button2.setTextColor(Color.BLACK)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = LogInFragment()
    }
}
