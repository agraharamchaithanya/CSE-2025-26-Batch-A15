package com.example.greenqrsmarttreeinformationsystem.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenqrsmarttreeinformationsystem.R
import com.example.greenqrsmarttreeinformationsystem.data.TreeResponse
import com.example.greenqrsmarttreeinformationsystem.databinding.FragmentUserMainBinding
import com.example.greenqrsmarttreeinformationsystem.logic.UserTreeAdapter
import com.example.greenqrsmarttreeinformationsystem.network.ServerViewModel
import com.example.greenqrsmarttreeinformationsystem.utility.showToast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class UserMainFragment : Fragment() {

    private var _binding: FragmentUserMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var treeAdapter: UserTreeAdapter
    private val viewModel by viewModels<ServerViewModel>()

    private val prefsKey = "oxygenPrefs"
    private val treeListKey = "storedTrees"
    private val lastUpdateTimeKey = "lastUpdateTime"

    private val dailyKey = "dailyOxygen"
    private val weeklyKey = "weeklyOxygen"
    private val monthlyKey = "monthlyOxygen"
    private val yearlyKey = "yearlyOxygen"

    private val sixHoursMillis = 6 * 60 * 60 * 1000L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

        binding.qrCodeLayout.setOnClickListener {
            findNavController().navigate(R.id.action_userMainFragment_to_userDashboardFragment)
        }

        binding.profileLayout.setOnClickListener {
            findNavController().navigate(R.id.action_userMainFragment_to_profileFragment)
        }

        viewModel.getTreeDetails("getTreeDetails")
        observeTreeList()
        updateOxygenValues()

    }

    private fun observeTreeList() {
        viewModel.treeResult.observe(viewLifecycleOwner) { result ->

            val fetchedTrees =
                (result.data as? MutableList<TreeResponse.Tree>)?.toMutableList()
                    ?: mutableListOf()

            saveTreesOffline(fetchedTrees)

            if (!::treeAdapter.isInitialized) {

                treeAdapter = UserTreeAdapter(fetchedTrees) { treeId ->
                    val bundle = Bundle()
                    bundle.putInt("treeId", treeId)
                    findNavController().navigate(
                        R.id.action_userMainFragment_to_treeDetailsFragment,
                        bundle
                    )
                }

                binding.treeRecyclerView.layoutManager =
                    LinearLayoutManager(requireActivity())
                binding.treeRecyclerView.adapter = treeAdapter

                // ✅ Initialize after adapter ready
                setupSearchView()

            } else {
                treeAdapter.updateList(fetchedTrees)
            }

            updateOxygenValues()
        }

        viewModel.treeError.observe(viewLifecycleOwner) { errorMsg ->
            showToast(errorMsg)
        }
    }

    private fun setupSearchView() {
        binding.searchId.addTextChangedListener {
            val query = it.toString()
            if (::treeAdapter.isInitialized) {
                treeAdapter.filter(query)
            }
        }
    }


    // ------------------ Offline Storage ------------------

    private fun saveTreesOffline(trees: List<TreeResponse.Tree>) {
        val prefs =
            requireActivity().getSharedPreferences(prefsKey, Context.MODE_PRIVATE)
        val gson = Gson()
        val treeJson = gson.toJson(trees)
        prefs.edit().putString(treeListKey, treeJson).apply()
    }

    private fun getStoredTrees(): MutableList<TreeResponse.Tree> {
        val prefs =
            requireActivity().getSharedPreferences(prefsKey, Context.MODE_PRIVATE)
        val json = prefs.getString(treeListKey, null)
        return if (json != null) {
            val type =
                object : TypeToken<MutableList<TreeResponse.Tree>>() {}.type
            Gson().fromJson(json, type)
        } else mutableListOf()
    }

    // ------------------ 6 Hours Oxygen Calculation ------------------

    private fun updateOxygenValues() {

        val trees = getStoredTrees()
        val totalTrees = trees.size
        binding.tvTotalTreesCount.text = "Total Trees: $totalTrees"

        var totalYearly = 0.0
        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH) + 1
        val seasonalFactor = getSeasonalFactor(month)

        trees.forEach { tree ->

            val rawText = tree.oxygenLevel?.trim() ?: "0"

// remove everything except number, dot and dash
            val cleaned = rawText.replace("[^0-9.-]".toRegex(), "")

            val oxygen = if (cleaned.contains("-")) {
                val parts = cleaned.split("-")
                val min = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
                val max = parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0
                (min + max) / 2
            } else {
                cleaned.toDoubleOrNull() ?: 0.0
            }

            android.util.Log.d("OXYGEN_DEBUG", "Tree: ${tree.name}, Oxygen Parsed: $oxygen")

            val healthFactor = getHealthFactor(tree.healthStatus)

            totalYearly += oxygen * seasonalFactor * healthFactor
        }

        val daily = totalYearly / 365
        val weekly = daily * 7
        val monthly = totalYearly / 12

        binding.tvDailyOxygen.text = String.format("%.2f kg", daily)
        binding.tvWeeklyOxygen.text = String.format("%.2f kg", weekly)
        binding.tvMonthlyOxygen.text = String.format("%.2f kg", monthly)
        binding.tvYearlyOxygen.text = String.format("%.2f kg", totalYearly)
    }

    private fun setOxygenToUI(
        daily: Double,
        weekly: Double,
        monthly: Double,
        yearly: Double
    ) {
        binding.tvDailyOxygen.text = String.format("%.2f kg", daily)
        binding.tvWeeklyOxygen.text = String.format("%.2f kg", weekly)
        binding.tvMonthlyOxygen.text = String.format("%.2f kg", monthly)
        binding.tvYearlyOxygen.text = String.format("%.2f kg", yearly)
    }

    private fun getSeasonalFactor(month: Int): Double {
        return when (month) {
            3, 4, 5, 6 -> 1.2
            7, 8, 9 -> 1.1
            10, 11 -> 1.0
            12, 1, 2 -> 0.8
            else -> 1.0
        }
    }

    private fun getHealthFactor(status: String?): Double {
        return when (status?.lowercase()) {
            "healthy" -> 1.0
            "average" -> 0.8
            "poor" -> 0.5
            else -> 1.0
        }
    }

    override fun onResume() {
        super.onResume()
        updateOxygenValues()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}