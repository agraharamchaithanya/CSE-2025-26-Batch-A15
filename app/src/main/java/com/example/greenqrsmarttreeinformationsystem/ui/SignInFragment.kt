package com.example.greenqrsmarttreeinformationsystem.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.greenqrsmarttreeinformationsystem.R
import com.example.greenqrsmarttreeinformationsystem.databinding.FragmentSignInBinding
import com.example.greenqrsmarttreeinformationsystem.network.ServerViewModel
import com.example.greenqrsmarttreeinformationsystem.utility.showToast

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ServerViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.linkToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_loginFragment)
        }

        binding.SignButton.setOnClickListener {
            val username = binding.nameEditText.text.toString().trim()
            val phone = binding.phoneEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            when {
                username.isEmpty() -> {showToast("please enter your name")}
                phone.isEmpty() -> {showToast("please enter your phone number")}
                email.isEmpty() -> {showToast("please enter your email")}
                password.isEmpty() -> {showToast("please enter your password")}
                else -> {
                    viewModel.registerUser(username, phone, email, password)
                    observerList()
                }
            }
        }
    }

    private fun observerList() {
        viewModel.registrationResult.observe(viewLifecycleOwner) {
            showToast(it.message)
        }
        viewModel.error.observe(viewLifecycleOwner) {
            showToast(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding= null
    }
}