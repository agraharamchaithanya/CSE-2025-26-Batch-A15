package com.example.greenqrsmarttreeinformationsystem.logic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.greenqrsmarttreeinformationsystem.data.TreeResponse
import com.example.greenqrsmarttreeinformationsystem.databinding.ItemTreeCardBinding

class TreeListAdapter(
    var list: MutableList<TreeResponse.Tree>,
    var view: (Int) -> Unit,
    var code: (Int) -> Unit,
    var delete: (Int, Int) -> Unit
) : RecyclerView.Adapter<TreeListAdapter.MainViewHolder>() {

    private var originalList: MutableList<TreeResponse.Tree> = list.toMutableList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainViewHolder {
        val binder = ItemTreeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(binder)
    }

    override fun onBindViewHolder(
        holder: MainViewHolder,
        position: Int
    ) {
        val item = list[position]
        with(holder.binding) {
            tvTreeName.text = item.name
            tvScientificName.text = item.scientificName
            tvDescription.text = item.description
            tvMedicinalUses.text = item.medicinalUses
            tvLocation.text = item.address
            tvHeight.text = "${item.height}m"
            tvOxygen.text = "${item.oxygenLevel} %"
            tvDateAdded.text = item.datePlanted
            btnView.setOnClickListener {
                view(item.id)
            }

            scanCode.setOnClickListener {
                code(item.id)
            }

            btnDelete.setOnClickListener {
                delete(item.id, holder.adapterPosition)
            }
            Glide.with(holder.itemView.context).load(item.imageUri).into(ivTreeImage)
        }
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, list.size)
    }

    fun filter(query: String) {

        list.clear()
        if(query.isEmpty()) {
            list.addAll(originalList)
        }else {
            list.addAll(
                originalList.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.scientificName.contains(query, ignoreCase = true)
                }
            )
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size
    class MainViewHolder(val binding: ItemTreeCardBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}