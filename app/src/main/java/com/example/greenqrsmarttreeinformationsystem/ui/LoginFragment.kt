package com.example.greenqrsmarttreeinformationsystem.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.greenqrsmarttreeinformationsystem.R
import com.example.greenqrsmarttreeinformationsystem.data.AuthResponse
import com.example.greenqrsmarttreeinformationsystem.databinding.FragmentLoginBinding
import com.example.greenqrsmarttreeinformationsystem.network.ServerViewModel
import com.example.greenqrsmarttreeinformationsystem.utility.savePref
import com.example.greenqrsmarttreeinformationsystem.utility.showToast

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                showToast("Camera permission granted")
                // You can navigate to QR scanner fragment here
            } else {
                showToast("Camera permission denied")
            }
        }

    private val viewModel by viewModels<ServerViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

        val sharedPref = savePref().getString("role", null) // default null, not "USER"

        if (!sharedPref.isNullOrEmpty()) {
            navigationRoute(sharedPref)  // user already logged in, skip login
        } else {
            showToast("Welcome! Please log in.")
        }
        binding.linkToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signInFragment)
        }

        checkCameraPermission()

        binding.loginButton.setOnClickListener {
            val userEmail = binding.emailEditText.text.toString().trim()
            val userPassword = binding.passwordEditText.text.toString().trim()
            when {
                userEmail.isEmpty() -> {
                    showToast("please enter username")
                }

                userPassword.isEmpty() -> {
                    showToast("please enter password")
                }

                userEmail.isNotEmpty() && userEmail == "Admin" && userPassword.isNotEmpty() && userPassword == "Admin@srit" -> {
                    saveRole("ADMIN")
                    findNavController().navigate(
                        R.id.action_loginFragment_to_treeListFragment,
                        null,
                        androidx.navigation.NavOptions.Builder()
                            .setPopUpTo(R.id.loginFragment, true).build()
                    )
                }

                else -> {
                    loginStatus(userEmail, userPassword)
                    observerList()
                }
            }
        }
    }

    private fun loginStatus(email: String, password: String) {
        if(email.contains("@srit.ac.in")){
            viewModel.loginUser(email, password, "LoginData")
        }else {
            showToast("invalid email credential, email should contain @srit.ac.in")
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                showToast("Camera permission already granted")
                // Navigate to scanner screen if needed
            }

            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }


    private fun observerList() {
        viewModel.loginResult.observe(viewLifecycleOwner) {
            if (it.error) {
                val data = it.data[0]
                showToast(it.message)
                saveRole("USER")
                saveProfile(data)
                findNavController().navigate(
                    R.id.action_loginFragment_to_userMainFragment,
                    null,
                    androidx.navigation.NavOptions.Builder().setPopUpTo(R.id.loginFragment, true)
                        .build()
                )
            }
        }

        viewModel.loginError.observe(viewLifecycleOwner) {
            showToast(it)
        }
    }

    private fun saveRole(role: String) {
        savePref().edit {
            putString("role", role)
            apply()
        }
    }

    private fun navigationRoute(role: String) {
        if (role == "ADMIN") {
            findNavController().navigate(R.id.action_loginFragment_to_treeListFragment)
        } else {
            findNavController().navigate(R.id.action_loginFragment_to_userMainFragment)
        }
    }

    private fun saveProfile(Auth: AuthResponse.Auth) {
        savePref().edit{
            putString("id", Auth.id.toString())
            putString("username", Auth.username)
            putString("phone", Auth.phone)
            putString("email", Auth.email)
            putString("password", Auth.password)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}