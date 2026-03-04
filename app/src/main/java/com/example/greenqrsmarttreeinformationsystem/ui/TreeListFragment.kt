package com.example.greenqrsmarttreeinformationsystem.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenqrsmarttreeinformationsystem.R
import com.example.greenqrsmarttreeinformationsystem.data.TreeResponse
import com.example.greenqrsmarttreeinformationsystem.databinding.FragmentTreeListBinding
import com.example.greenqrsmarttreeinformationsystem.logic.TreeListAdapter
import com.example.greenqrsmarttreeinformationsystem.network.ServerViewModel
import com.example.greenqrsmarttreeinformationsystem.utility.backPush
import com.example.greenqrsmarttreeinformationsystem.utility.savePref
import com.example.greenqrsmarttreeinformationsystem.utility.showToast

class TreeListFragment : Fragment() {

    private var _binding: FragmentTreeListBinding? = null
    private val binding get() = _binding!!

    private lateinit var treeAdapter: TreeListAdapter
    private val viewModel by activityViewModels<ServerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTreeListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

        binding.btnAddTree.setOnClickListener {
            findNavController().navigate(R.id.action_treeListFragment_to_addTreesFragment)
        }

        binding.logout.setOnClickListener {
            savePref().edit {
                clear()
            }
            findNavController().navigate(R.id.action_treeListFragment_to_loginFragment)
        }

        binding.btnViewFeedback.setOnClickListener {
            findNavController().navigate(R.id.action_treeListFragment_to_getFeedbackFragment)
        }

        viewModel.getTreeDetails("getTreeDetails")
        observerList()
        setupSearchView()

    }

    private fun observerList() {
        viewModel.treeResult.observe(viewLifecycleOwner) {
            treeAdapter = TreeListAdapter(it.data as MutableList<TreeResponse.Tree>, {
                val findNavController = findNavController()
                val bundle = Bundle()
                bundle.putInt("treeId", it)
                findNavController.navigate(
                    R.id.action_treeListFragment_to_treeDetailsFragment,
                    bundle
                )
            }, {treeId ->

                val findNavController = findNavController()
                val bundle = Bundle()
                bundle.putInt("treeId", treeId)
                findNavController.navigate(
                    R.id.action_treeListFragment_to_viewQrCodeFragment,
                    bundle
                )
            },{ it, position ->
                viewModel.deleteTree("deleteTree", it)

                viewModel.deleteTree.observe(viewLifecycleOwner) {
                    if (it.error) {
                        treeAdapter.removeItem(position)
                        showToast("tree deleted")
                    } else {
                        showToast(it.message)
                    }
                }
            })
            if (it.error) {
                showToast(it.message)
                binding.rvTrees.apply {
                    layoutManager = LinearLayoutManager(requireActivity())
                    adapter = treeAdapter
                }
            } else {
                showToast(it.message)
            }

            binding.tvTotalTrees.text = it.data.size.toString()
        }

        viewModel.treeError.observe(viewLifecycleOwner) {
            showToast(it)
        }
    }

    private fun setupSearchView() {
        binding.etSearch.addTextChangedListener {
            val query = it.toString()
            treeAdapter.filter(query)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}