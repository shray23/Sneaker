package com.example.sneakersapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.sneakersapp.R
import com.example.sneakersapp.data.SneakerData
import com.example.sneakersapp.databinding.HomeSneakerListItemRowBinding
import com.example.sneakersapp.util.HOME_ADAPTER_ADD_TO_CART
import com.example.sneakersapp.util.HOME_ADAPTER_ITEM_SELECTED
import java.util.ArrayList

class HomeAdapter(private val mContext: Context?,
                  private val mClickCallback: ((Int, SneakerData?) -> Unit)) :
    RecyclerView.Adapter<HomeAdapter.MainViewHolder>() {

    private var sneakerDataItems: MutableList<SneakerData> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val binding = DataBindingUtil.inflate<HomeSneakerListItemRowBinding>(
            LayoutInflater.from(parent.getContext()),
            R.layout.home_sneaker_list_item_row, parent, false
        )
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        when (holder) {
            is MainViewHolder -> {
                val sneakerData = sneakerDataItems.get(position)
                holder.itemRowBinding.item = sneakerData

                holder.itemRowBinding.layoutRoot.setOnClickListener {
                    mClickCallback.invoke(HOME_ADAPTER_ITEM_SELECTED, sneakerData)
                }

                holder.itemRowBinding.ivAddToCart.setOnClickListener {
                    mClickCallback.invoke(HOME_ADAPTER_ADD_TO_CART, sneakerData)
                }
                mContext?.let {
                    Glide
                        .with(it)
                        .load(sneakerData.media?.thumbUrl)
                        .placeholder(R.drawable.ic_placeholder)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .skipMemoryCache(true)
                        .into(holder.itemRowBinding.ivShoe)
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return sneakerDataItems.size
    }

    inner class MainViewHolder constructor(var itemRowBinding: HomeSneakerListItemRowBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root)


    fun submitList(sneakerDataList: List<SneakerData>) {
        this.sneakerDataItems.clear()
        this.sneakerDataItems.addAll(sneakerDataList)
        notifyDataSetChanged()
    }
}