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
import com.example.book_slide.DataClasses.Users.Users
import com.example.book_slide.DataClasses.Users.UsersDatabase
import com.example.book_slide.DataClasses.Users.UsersRepository
import com.example.book_slide.DataClasses.Users.UsersViewModel
import com.example.book_slide.DataClasses.Users.UsersViewModelFactory
import com.example.book_slide.databinding.FragmentRegisterBinding
import com.example.book_slide.util.ColorBlindnessManager
import com.example.book_slide.util.ColorBlindnessMode
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
            val email = binding.etRegisterEmail.text.toString()
            val password = binding.etRegisterPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                saveUser(email, password)
            } else {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
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

    private fun saveUser(email: String, password: String) {
        val user = Users(
            email = email,
            password = password
        )
        viewModel.registerUser(user)

        Toast.makeText(requireContext(), "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
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
