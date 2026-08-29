package com.example.book_slide.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.R
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.book_slide.DataClasses.Users.Users
import com.example.book_slide.databinding.FragmentRegisterBinding
import com.example.book_slide.DataClasses.Users.UsersRepository
import com.example.book_slide.DataClasses.Users.UsersViewModel
import com.example.book_slide.DataClasses.Users.UsersViewModelFactory
import com.example.book_slide.DataClasses.Users.UsersDatabase
import com.example.book_slide.fragment.RegisterFragment


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

    private fun saveUser(email: String, password: String) {
        val user = Users(
            email = email,
            password = password
        )
        viewModel.registerUser(user)

        Toast.makeText(requireContext(), "Usuario registrado existosamente", Toast.LENGTH_SHORT).show()
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