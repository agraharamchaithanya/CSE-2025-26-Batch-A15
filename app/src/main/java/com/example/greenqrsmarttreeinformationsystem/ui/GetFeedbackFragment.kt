package com.example.greenqrsmarttreeinformationsystem.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenqrsmarttreeinformationsystem.R
import com.example.greenqrsmarttreeinformationsystem.data.FeedbackResponse
import com.example.greenqrsmarttreeinformationsystem.data.TreeResponse
import com.example.greenqrsmarttreeinformationsystem.databinding.FragmentGetFeedbackBinding
import com.example.greenqrsmarttreeinformationsystem.logic.FeedbackAdapter
import com.example.greenqrsmarttreeinformationsystem.logic.UserTreeAdapter
import com.example.greenqrsmarttreeinformationsystem.network.ServerViewModel
import com.example.greenqrsmarttreeinformationsystem.utility.showToast
import kotlin.getValue

class GetFeedbackFragment : Fragment() {
    private var _binding: FragmentGetFeedbackBinding? = null
    private val binding get() = _binding!!


    private lateinit var treeAdapter: FeedbackAdapter

    private val viewModel by viewModels<ServerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGetFeedbackBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFeedbackReview("feedbacks")


        observerList()
        setupSearchView()
    }


    private fun observerList() {
        viewModel.getFeedback.observe(viewLifecycleOwner) {
            treeAdapter = FeedbackAdapter(it.data as MutableList<FeedbackResponse.Feedback>) { treeId ->

                    val findNavController = findNavController()
                    val bundle = Bundle()
                    bundle.putInt("treeId", treeId)
                    findNavController.navigate(
                        R.id.action_getFeedbackFragment_to_treeDetailsFragment,
                        bundle
                    )
            }

            if (it.error) {

                showToast(it.message)
                binding.totalFeeds.text = "Total: ${it.data.size} feedbacks"
                binding.recyclerViewFeedback.apply {
                    layoutManager = LinearLayoutManager(requireActivity())
                    adapter = treeAdapter
                }
            } else {
                showToast(it.message)
            }

        }

        viewModel.treeError.observe(viewLifecycleOwner) {
            showToast(it)
        }
    }

    private fun setupSearchView() {
        binding.searchFeed.addTextChangedListener {
            val query = it.toString()
            treeAdapter.filter(query)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}