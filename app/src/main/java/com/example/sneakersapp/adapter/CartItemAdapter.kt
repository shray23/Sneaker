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
import com.example.sneakersapp.databinding.CartListItemRowBinding
import com.example.sneakersapp.util.HOME_ADAPTER_ITEM_SELECTED
import com.example.sneakersapp.util.REMOVE_ITEM_FROM_CART
import java.util.ArrayList

class CartItemAdapter(
    private val mContext: Context?,
    private val mClickCallback: ((Int, Int, SneakerData?) -> Unit)
) :
    RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {

    private var sneakerDataItems: MutableList<SneakerData> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = DataBindingUtil.inflate<CartListItemRowBinding>(
            LayoutInflater.from(parent.getContext()),
            R.layout.cart_list_item_row, parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        when (holder) {
            is CartViewHolder -> {
                val sneakerData = sneakerDataItems.get(position)
                holder.itemRowBinding.item = sneakerData

                holder.itemRowBinding.ivCross.setOnClickListener {
                    mClickCallback.invoke(REMOVE_ITEM_FROM_CART, position, sneakerData)
                    notifyItemRemoved(position)
                }
                mContext?.let {
                    Glide
                        .with(it)
                        .load(sneakerData.media?.smallImageUrl)
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

    inner class CartViewHolder constructor(var itemRowBinding: CartListItemRowBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root)


    fun submitList(sneakerDataList: List<SneakerData>) {
        this.sneakerDataItems.clear()
        this.sneakerDataItems.addAll(sneakerDataList)
    }
}