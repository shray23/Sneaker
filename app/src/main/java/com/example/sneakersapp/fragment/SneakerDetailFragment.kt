package com.example.sneakersapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.sneakersapp.R
import com.example.sneakersapp.activity.MainActivity
import com.example.sneakersapp.adapter.SneakerColourAdapter
import com.example.sneakersapp.adapter.SneakerSizeAdapter
import com.example.sneakersapp.data.SneakerData
import com.example.sneakersapp.data.SneakerID
import com.example.sneakersapp.databinding.LayoutSneakerDetailFragmentBinding
import com.example.sneakersapp.util.AutoClearedValue
import com.example.sneakersapp.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SneakerDetailFragment : Fragment() {
    var mBinding: AutoClearedValue<LayoutSneakerDetailFragmentBinding>? =
        null  //to prevent memory leaks

    val sneakerData: SneakerData? by lazy {
        SneakerDetailFragmentArgs.fromBundle(requireArguments()).data
    }

    lateinit var mSizeAdapter: SneakerSizeAdapter

    lateinit var mColourAdapter: SneakerColourAdapter

    lateinit var mViewModel: HomeViewModel

    private var isShowProgress = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        DataBindingUtil.inflate<LayoutSneakerDetailFragmentBinding>(
            inflater, R.layout.layout_sneaker_detail_fragment, container, false
        ).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.sneakerItem = sneakerData
            it.showProgress = isShowProgress
            mBinding = AutoClearedValue(this, it)
        }.apply {
            return this.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(
            HomeViewModel::class.java
        )

        context?.let {
            mBinding?.get()?.ivShoe?.let { it1 ->
                Glide
                    .with(it)
                    .load(sneakerData?.media?.imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .skipMemoryCache(true)
                    .into(it1)
            }
        }

        mBinding?.get()?.btnAddtocart?.setOnClickListener {
            isShowProgress.value = true
            lifecycleScope.launch {
                insertItemToCart(sneakerData)
            }
        }
        setSizeAdapter()
        setColourAdapter()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { //enabling back in toolbar
            (it as AppCompatActivity).setSupportActionBar(mBinding?.get()?.toolbar)
            it.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            it.supportActionBar?.setDisplayShowHomeEnabled(true)
            mBinding?.get()?.toolbar?.setNavigationOnClickListener { requireActivity().onBackPressed() }
        }
    }

    fun setSizeAdapter() {
        sneakerData?.size?.let {
            if (!::mSizeAdapter.isInitialized) {
                mSizeAdapter = SneakerSizeAdapter(mContext = context)
            }
            mBinding?.get()?.sneakerSizeAdapter = mSizeAdapter
            mSizeAdapter.submitList(it)
        }
    }

    fun setColourAdapter() {
        sneakerData?.colorway?.let {
            if (!::mColourAdapter.isInitialized) {
                mColourAdapter = SneakerColourAdapter(mContext = context)
            }
            mBinding?.get()?.sneakerColourAdapter = mColourAdapter
            mColourAdapter.submitList(it)
        }
    }

    suspend fun insertItemToCart(data: SneakerData?) {
        withContext(Dispatchers.IO) {
            data?.let {
                if (it.id != null && it.id.isNotEmpty()) {
                    mViewModel.insertSneakerDataIntoCart(SneakerID(it.id))

                    withContext(Dispatchers.Main) {
                        isShowProgress.value = false
                        Toast.makeText(
                            context, getString(R.string.item_added_to_cart), Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}