package com.example.greenqrsmarttreeinformationsystem.ui

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Base64.NO_WRAP
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.greenqrsmarttreeinformationsystem.R
import com.example.greenqrsmarttreeinformationsystem.databinding.FragmentAddTreesBinding
import com.example.greenqrsmarttreeinformationsystem.network.ServerViewModel
import com.example.greenqrsmarttreeinformationsystem.utility.showToast
import android.graphics.Bitmap
import android.provider.MediaStore
import java.io.ByteArrayOutputStream

class AddTreesFragment : Fragment() {

    private var _binding: FragmentAddTreesBinding? = null
    private val binding get() = _binding!!

    private var selectedImage: Uri? = null
    private val viewModel: ServerViewModel by viewModels()

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            Glide.with(this).load(it).into(binding.ivTreeImage)
            selectedImage = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTreesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnUploadImage.setOnClickListener { launcher.launch("image/*") }

        binding.btnSubmit.setOnClickListener {

            val treeName = binding.etTreeName.text.toString().trim()
            val scientificName = binding.etScientificName.text.toString().trim()
            val medicinalUses = binding.etMedicinalUses.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val latitude = binding.etLatitude.text.toString().trim()
            val longitude = binding.etLongitude.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()
            val height = binding.etHeight.text.toString().trim()
            val oxygenLevel = binding.etOxygenLevel.text.toString().trim()
            val diameter = binding.etDiameter.text.toString().trim()
            val canopySpread = binding.etCanopySpread.text.toString().trim()
            val speciesType = binding.etSpecies.text.toString().trim()
            val waterNeeds = binding.etWaterNeeds.text.toString().trim()
            val datePlanted = binding.etDatePlanted.text.toString().trim()
            val conservStatus = binding.etStatus.text.toString().trim()
            val healthStatus = binding.etHealth.text.toString().trim()
            val treeId = binding.etTreeId.text.toString().trim()

            when {
                selectedImage == null -> showToast("Please select a tree image")
                treeName.isEmpty() -> showToast("Please enter tree name")
                scientificName.isEmpty() -> showToast("Please enter scientific name")
                latitude.isEmpty() || longitude.isEmpty() -> showToast("Please enter location details")
                treeId.isEmpty() -> showToast("Please enter tree id")

                !oxygenLevel.matches(Regex("^[0-9.]+(-[0-9.]+)?$")) ->
                    showToast("Enter valid oxygen value (e.g., 0.5 or 0.5-1)")

                else -> {

                    binding.progressBar.isVisible = true

                    selectedImage?.let { uri ->
                        try {

                            // 🔥 Convert to Bitmap
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                requireActivity().contentResolver,
                                uri
                            )

                            // 🔥 Resize (Safe Size)
                            val resizedBitmap = Bitmap.createScaledBitmap(
                                bitmap,
                                800,
                                800,
                                true
                            )

                            // 🔥 Compress
                            val stream = ByteArrayOutputStream()
                            resizedBitmap.compress(
                                Bitmap.CompressFormat.JPEG,
                                60,
                                stream
                            )

                            val base64Image = Base64.encodeToString(
                                stream.toByteArray(),
                                Base64.NO_WRAP
                            )

                            // 🔥 Send to Server
                            viewModel.addTreeDetails(
                                name = treeName,
                                scientificName = scientificName,
                                medicinalUses = medicinalUses.ifEmpty { "N/A" },
                                description = description.ifEmpty { "N/A" },
                                imageUri = base64Image,
                                latitude = latitude,
                                longitude = longitude,
                                address = address.ifEmpty { "N/A" },
                                height = height.ifEmpty { "N/A" },
                                oxygenLevel = oxygenLevel,
                                diameter = diameter.ifEmpty { "N/A" },
                                canopySpread = canopySpread.ifEmpty { "N/A" },
                                speciesType = speciesType.ifEmpty { "N/A" },
                                waterNeeds = waterNeeds.ifEmpty { "N/A" },
                                datePlanted = datePlanted.ifEmpty { "N/A" },
                                conservStatus = conservStatus.ifEmpty { "N/A" },
                                healthStatus = healthStatus.ifEmpty { "N/A" },
                                treeId = treeId
                            ) { response ->

                                binding.progressBar.isVisible = false

                                if (!response.error) {
                                    showToast(response.message)
                                    clearFields()
                                } else {
                                    showToast("Failed: ${response.message}")
                                }
                            }

                        } catch (e: Exception) {
                            binding.progressBar.isVisible = false
                            showToast("Image processing failed")
                        }
                    }
                }
            }
        }
    }

    private fun clearFields() {
        with(binding) {
            etTreeName.text?.clear()
            etScientificName.text?.clear()
            etMedicinalUses.text?.clear()
            etDescription.text?.clear()
            etLatitude.text?.clear()
            etLongitude.text?.clear()
            etAddress.text?.clear()
            etHeight.text?.clear()
            etOxygenLevel.text?.clear()
            etDiameter.text?.clear()
            etCanopySpread.text?.clear()
            etSpecies.text?.clear()
            etDatePlanted.text?.clear()
            etStatus.text?.clear()
            etHealth.text?.clear()
            etTreeId.text?.clear()
            ivTreeImage.setImageResource(R.drawable.tree)
            selectedImage = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}