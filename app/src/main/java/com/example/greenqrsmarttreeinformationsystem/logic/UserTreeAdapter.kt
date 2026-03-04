package com.example.greenqrsmarttreeinformationsystem.logic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.greenqrsmarttreeinformationsystem.data.TreeResponse
import com.example.greenqrsmarttreeinformationsystem.databinding.TreeCardBinding

class UserTreeAdapter(
    private var treeList: MutableList<TreeResponse.Tree>,
    private val treeId: (Int) -> Unit
) : RecyclerView.Adapter<UserTreeAdapter.MainViewHolder>() {

    // Keep a copy for filtering
    private var originalList: MutableList<TreeResponse.Tree> = treeList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val bind = TreeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(bind)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = treeList[position]
        with(holder.binding) {
            treeName.text = item.name
            etScientificName.text = item.scientificName
            healthStatus.text = item.healthStatus
            description.text = item.description
            Glide.with(holder.binding.root.context).load(item.imageUri).into(treeImage)
        }

        holder.binding.cardId.setOnClickListener {
            treeId(item.id)
        }
    }

    override fun getItemCount(): Int = treeList.size

    // Filter trees by name or scientific name
    fun filter(query: String) {
        treeList.clear()
        if (query.isEmpty()) {
            treeList.addAll(originalList)
        } else {
            treeList.addAll(
                originalList.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.scientificName.contains(query, ignoreCase = true)
                }
            )
        }
        notifyDataSetChanged()
    }

    // Update the adapter list dynamically
    fun updateList(newList: List<TreeResponse.Tree>) {
        treeList.clear()
        treeList.addAll(newList)
        originalList = newList.toMutableList()
        notifyDataSetChanged()
    }

    class MainViewHolder(val binding: TreeCardBinding) : RecyclerView.ViewHolder(binding.root)
}