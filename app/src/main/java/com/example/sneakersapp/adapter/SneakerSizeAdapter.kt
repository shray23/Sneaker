package com.example.sneakersapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sneakersapp.R
import com.example.sneakersapp.databinding.SizeListItemRowBinding
import java.util.ArrayList

class SneakerSizeAdapter(private val mContext: Context?) :
    RecyclerView.Adapter<SneakerSizeAdapter.SizeViewHolder>() {

    private var sneakerDataItems: MutableList<String> = ArrayList()
    private var previousSelectedSize: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        val binding = DataBindingUtil.inflate<SizeListItemRowBinding>(
            LayoutInflater.from(parent.getContext()),
            R.layout.size_list_item_row, parent, false
        )
        return SizeViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SizeViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        when (holder) {
            is SizeViewHolder -> {
                val sneakerData = sneakerDataItems.get(position)
                holder.itemRowBinding.size = sneakerData
                holder.itemRowBinding.isSelected = (position == previousSelectedSize)

                holder.itemRowBinding.layoutRoot.setOnClickListener {
                    holder.itemRowBinding.isSelected = true
                    notifyItemChanged(position)
                    if (previousSelectedSize != -1) {
                        notifyItemChanged(previousSelectedSize)
                    }
                    previousSelectedSize = position
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return sneakerDataItems.size
    }

    inner class SizeViewHolder constructor(var itemRowBinding: SizeListItemRowBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root)


    fun submitList(sizeDataList: List<String>) {
        this.sneakerDataItems.clear()
        this.sneakerDataItems.addAll(sizeDataList)
        notifyDataSetChanged()
    }
}