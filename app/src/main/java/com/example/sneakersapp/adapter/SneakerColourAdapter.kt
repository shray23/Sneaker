package com.example.sneakersapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sneakersapp.R
import com.example.sneakersapp.databinding.SizeListItemRowBinding
import com.example.sneakersapp.databinding.SneakerColourItemRowBinding
import java.util.ArrayList

class SneakerColourAdapter(private val mContext: Context?) :
    RecyclerView.Adapter<SneakerColourAdapter.ColourViewHolder>() {

    private var colorDataItem: MutableList<String> = ArrayList()
    private var previousSelectedColor: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColourViewHolder {
        val binding = DataBindingUtil.inflate<SneakerColourItemRowBinding>(
            LayoutInflater.from(parent.getContext()),
            R.layout.sneaker_colour_item_row, parent, false
        )
        return ColourViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ColourViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        when (holder) {
            is ColourViewHolder -> {
                val color = colorDataItem.get(position)
                holder.itemRowBinding.layoutRoot.setBackgroundResource(R.drawable.rounded_button)
                holder.itemRowBinding.layoutRoot.setBackgroundColor(Color.parseColor(color))
                holder.itemRowBinding.isSelected = (position == previousSelectedColor)

                holder.itemRowBinding.layoutRoot.setOnClickListener {
                    holder.itemRowBinding.isSelected = true
                    notifyItemChanged(position)
                    if (previousSelectedColor != -1) {
                        notifyItemChanged(previousSelectedColor)
                    }
                    previousSelectedColor = position
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return colorDataItem.size
    }

    inner class ColourViewHolder constructor(var itemRowBinding: SneakerColourItemRowBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root)


    fun submitList(colorlist: List<String>) {
        this.colorDataItem.clear()
        this.colorDataItem.addAll(colorlist)
        notifyDataSetChanged()
    }
}