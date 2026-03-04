package com.example.greenqrsmarttreeinformationsystem.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.greenqrsmarttreeinformationsystem.R
import com.example.greenqrsmarttreeinformationsystem.databinding.FragmentProfileBinding
import com.example.greenqrsmarttreeinformationsystem.utility.savePref
import com.example.greenqrsmarttreeinformationsystem.utility.showToast

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backStack.setOnClickListener {
            findNavController().navigateUp()
        }

        with(savePref()) {
            binding.studentName.text = getString("username", "")
            binding.userName.text = getString("username", "")
            binding.email.text = getString("email", "")
            binding.phone.text = getString("phone", "")
            binding.password.text = getString("password", "")
        }

        binding.logout.setOnClickListener {
            savePref().edit {
                clear()
            }
            showToast("Logged out successfully")
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}