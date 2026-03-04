package com.example.greenqrsmarttreeinformationsystem.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.greenqrsmarttreeinformationsystem.R
import com.example.greenqrsmarttreeinformationsystem.databinding.FragmentTreeDetailsBinding
import com.example.greenqrsmarttreeinformationsystem.network.ServerViewModel
import com.example.greenqrsmarttreeinformationsystem.utility.showToast
import com.example.greenqrsmarttreeinformationsystem.data.TreeResponse

class TreeDetailsFragment : Fragment() {

    private var _binding: FragmentTreeDetailsBinding? = null
    private val binding get() = _binding!!

    private var fromScan = false

    private var currentTreeId = ""
    private var currentTreeName = ""
    private var currentScientificName = ""
    private var currentAddress = ""

    private val viewModel by activityViewModels<ServerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTreeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔙 Back
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        val treeIdFromBundle = arguments?.getInt("treeId") ?: run {
            showToast("Invalid QR Code")
            return
        }

        fromScan = arguments?.getBoolean("fromScan") ?: false

        if (fromScan) {

            val allTrees = viewModel.treeResult.value?.data

            val scannedTree = allTrees?.find { it.id == treeIdFromBundle }

            if (scannedTree != null) {
                bindTreeData(scannedTree)
            } else {
                showToast("Tree not found")
            }

        } else {
            viewModel.getSpecificTree("getSpecificTree", treeIdFromBundle)
        }

        // 🔗 Share
        binding.btnShareTree.setOnClickListener {
            val tree = viewModel.treeDetails.value?.data?.firstOrNull()
            if (tree != null) {
                val shareText = """
                    🌳 Tree Name: ${tree.name}
                    🧬 Scientific Name: ${tree.scientificName}
                    📏 Height: ${tree.height ?: "N/A"}
                    🗓 Date Planted: ${tree.datePlanted}
                    📝 Description: ${tree.description}
                    📍 Address: ${tree.address}
                """.trimIndent()
                shareTreeDetails(shareText)
            } else {
                showToast("Tree details not available")
            }
        }

        // 📝 Feedback
        binding.btFeedback.setOnClickListener {
            val feedbackText = binding.feedbackNotes.text.toString()
            val user = binding.userName.text.toString()

            when {
                feedbackText.isEmpty() -> showToast("Please enter feedback")
                user.isEmpty() -> showToast("Please enter your name")
                else -> {
                    viewModel.feedbackReview(
                        currentTreeId,
                        feedbackText,
                        user,
                        currentTreeName,
                        currentScientificName,
                        currentAddress
                    )
                }
            }
        }

        // Cached data (optional UX)
        viewModel.treeDetails.value?.data?.firstOrNull()?.let { tree ->
            binding.tvGridTreeName.text = tree.name
            binding.tvGridScientificName.text = tree.scientificName
            binding.tvTreeDescription.text = tree.description

            Glide.with(requireContext())
                .load(tree.imageUri)
                .placeholder(R.drawable.tree)
                .into(binding.ivTreeDetailImage)
        }

        observerList()
        feedList()

    }

    private fun observerList() {
        viewModel.treeDetails.observe(viewLifecycleOwner) { response ->
            if (response != null && response.data.isNotEmpty()) {

                val tree = response.data[0]

                with(binding) {
                    tvGridTreeName.text = tree.name
                    tvGridScientificName.text = tree.scientificName
                    tvTreeDescription.text = tree.description

                    tvTreeHeight.text =
                        if (!tree.height.isNullOrBlank())
                            "${tree.name} can grow up to ${tree.height} in height."
                        else
                            "Height information not available."

                    tvTreeOxygen.text =
                        if (!tree.oxygenLevel.isNullOrBlank())
                            "${tree.name} produces approximately ${tree.oxygenLevel} kg of oxygen per year(estimated)."
                        else
                            "Oxygen production data not available."

                    tvTreeAddress.text = tree.address
                    tvCoordinates.text = "${tree.latitude}, ${tree.longitude}"

                    tvTreeDiameter.text = tree.diameter?.let {
                        "The stem diameter of ${tree.name} is approximately ${tree.diameter}."
                    } ?: "Diameter information is not available."

                    tvTreeCanopy.text = tree.canopySpread?.let {
                        "The canopy of ${tree.name} spreads approximately ${tree.canopySpread} (canopy spread = shadow)."
                    } ?: "Canopy spread information is not available."

                    tvSpeciesType.text = tree.speciesType
                    tvWaterNeeds.text = tree.waterNeeds
                    tvDatePlanted.text = tree.datePlanted
                    tvConservationStatus.text = tree.conservStatus
                    chipTreeStatus.text = tree.healthStatus
                    tvTreeId.text = tree.treeId

                    tvMedicinalUses.text =
                        tree.medicinalUses?.takeIf { it.isNotBlank() }
                            ?.replace(",", "\n• ")
                            ?.let { "• $it" }
                            ?: "Information not provided"

                    Glide.with(requireContext())
                        .load(tree.imageUri)
                        .placeholder(R.drawable.tree)
                        .error(R.drawable.tree)
                        .into(ivTreeDetailImage)
                }

                currentTreeId = tree.id.toString()
                currentTreeName = tree.name
                currentScientificName = tree.scientificName
                currentAddress = tree.address
            }
        }

        viewModel.detailsError.observe(viewLifecycleOwner) {
            showToast(it)
        }
    }

    private fun feedList() {
        viewModel.feedback.observe(viewLifecycleOwner) {
            showToast(it.message)
            binding.feedbackNotes.text?.clear()
            binding.userName.text?.clear()
        }

        viewModel.feedError.observe(viewLifecycleOwner) {
            showToast(it)
        }
    }

    private fun shareTreeDetails(text: String) {
        val intent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(android.content.Intent.createChooser(intent, "Share via"))
    }

    private fun bindTreeData(tree: TreeResponse.Tree) {

        currentTreeId = tree.id.toString()
        currentTreeName = tree.name
        currentScientificName = tree.scientificName
        currentAddress = tree.address

        with(binding) {
            tvGridTreeName.text = tree.name
            tvGridScientificName.text = tree.scientificName
            tvTreeDescription.text = tree.description

            tvTreeHeight.text =
                if (!tree.height.isNullOrBlank())
                    "${tree.name} can grow up to ${tree.height} in height."
                else
                    "Height information not available."

            tvTreeOxygen.text =
                if (!tree.oxygenLevel.isNullOrBlank())
                    "${tree.name} produces approximately ${tree.oxygenLevel} kg of oxygen per year."
                else
                    "Oxygen production data not available."

            tvTreeAddress.text = tree.address
            tvCoordinates.text = "${tree.latitude}, ${tree.longitude}"
            tvTreeDiameter.text = tree.diameter
            tvTreeCanopy.text = tree.canopySpread
            tvSpeciesType.text = tree.speciesType
            tvWaterNeeds.text = tree.waterNeeds
            tvDatePlanted.text = tree.datePlanted
            tvConservationStatus.text = tree.conservStatus
            chipTreeStatus.text = tree.healthStatus
            tvTreeId.text = tree.treeId

            Glide.with(requireContext())
                .load(tree.imageUri)
                .placeholder(R.drawable.tree)
                .error(R.drawable.tree)
                .into(ivTreeDetailImage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}