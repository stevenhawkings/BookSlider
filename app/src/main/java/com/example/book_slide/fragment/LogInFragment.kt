package com.example.book_slide.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.book_slide.R
import com.example.book_slide.databinding.FragmentLogInBinding

class LogInFragment : Fragment() {

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

        binding.button.setOnClickListener {
            val email = binding.editTextText.text?.toString()?.trim()
            val password = binding.editTextTextPassword.text?.toString()

            val sharedPref = requireActivity().getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)
            val storedPassword = sharedPref.getString(email, null)

            if (storedPassword != null && storedPassword == password) {
                Toast.makeText(requireContext(), "Bienvenido de nuevo: $email", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.button2.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RegisterFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = LogInFragment()
    }
}
