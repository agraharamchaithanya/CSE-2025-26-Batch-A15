package com.example.greenqrsmarttreeinformationsystem.logic

import android.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.greenqrsmarttreeinformationsystem.data.FeedbackResponse
import com.example.greenqrsmarttreeinformationsystem.data.TreeResponse
import com.example.greenqrsmarttreeinformationsystem.databinding.FeedbackItemBinding

class FeedbackAdapter(var list: MutableList<FeedbackResponse.Feedback>, var treeId:(Int) -> Unit) : RecyclerView.Adapter<FeedbackAdapter.MainViewHolder>() {
    private var originalList: List<FeedbackResponse.Feedback> = list.toMutableList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainViewHolder {
        val bind = FeedbackItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MainViewHolder(bind)
    }

    override fun onBindViewHolder(
        holder: MainViewHolder,
        position: Int
    ) {
        val data = list[position]

        holder.binding.apply {
            tvTreeName.text = data.treeName
            tvScientificName.text = data.scientificName
            tvLocation.text = "📍 ${data.address}"
            tvUserName.text = data.userName
            tvFeedback.text = data.feedback
        }

        holder.binding.layoutCard.setOnClickListener {
            treeId(data.treeId.toInt())
        }
    }
    fun filter(query: String) {

        list.clear()
        if(query.isEmpty()) {
            list.addAll(originalList)
        }else {
            list.addAll(
                originalList.filter {
                    it.treeName.contains(query, ignoreCase = true) ||
                            it.scientificName.contains(query, ignoreCase = true)
                }
            )
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    class MainViewHolder(val binding: FeedbackItemBinding): RecyclerView.ViewHolder(binding.root) {

    }
}