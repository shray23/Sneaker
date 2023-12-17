package com.example.sneakersapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.example.sneakersapp.R
import com.example.sneakersapp.adapter.CartItemAdapter
import com.example.sneakersapp.data.SneakerData
import com.example.sneakersapp.data.SneakerID
import com.example.sneakersapp.databinding.LayoutCartFragmentBinding
import com.example.sneakersapp.network.Resource
import com.example.sneakersapp.network.Status
import com.example.sneakersapp.util.AutoClearedValue
import com.example.sneakersapp.util.REMOVE_ITEM_FROM_CART
import com.example.sneakersapp.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment() {
    lateinit var mAdapter: CartItemAdapter

    lateinit var mViewModel: HomeViewModel

    var mBinding: AutoClearedValue<LayoutCartFragmentBinding>? = null  //to prevent memory leaks

    var getCartItems: LiveData<List<SneakerData>>? = null

    private var isCartEmpty = MutableLiveData<Boolean>()

    private var isShowProgress = MutableLiveData<Boolean>()

    private var subtotalAmount = MutableLiveData<String>()

    private var totalAmount = MutableLiveData<String>()

    private var positionRemoved = -1

    val cartItemList: MutableList<SneakerData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        DataBindingUtil.inflate<LayoutCartFragmentBinding>(
            inflater, R.layout.layout_cart_fragment, container, false
        ).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.isCartEmpty = isCartEmpty
            it.subtotal = subtotalAmount
            it.total = totalAmount
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

        isCartEmpty.value = true
        setAdapter()

        mBinding?.get()?.btnAddtocart?.setOnClickListener {
            Toast.makeText(context, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
        }
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

    fun setAdapter() {
        if (!::mAdapter.isInitialized) {
            mAdapter = CartItemAdapter(mContext = context, mClickCallback = this::onItemSelected)
        }
        mBinding?.get()?.adapter = mAdapter
        lifecycleScope.launch {
            fetchData()
        }
    }

    fun onItemSelected(action: Int, position: Int, data: SneakerData?) {
        when (action) {
            REMOVE_ITEM_FROM_CART -> {
                data?.let {
                    lifecycleScope.launch {
                        removeCartItem(it)
                        positionRemoved = position
                    }
                }
            }
        }
    }

    suspend fun fetchData() {
        withContext(Dispatchers.IO) {
            getCartItems = mViewModel.getSneakerCartData()
            withContext(Dispatchers.Main) {
                getCartItems?.observe(viewLifecycleOwner, getCartItemObserver)
            }
        }
    }

    private val getCartItemObserver = object : Observer<List<SneakerData>?> {
        override fun onChanged(value: List<SneakerData>?) {
            if (value != null && value.size > 0) {
                getCartItems?.removeObserver(this)
                isCartEmpty.value = false
                mAdapter.submitList(value)
                mAdapter.notifyDataSetChanged()
            } else {
                isCartEmpty.value = true
            }
            updateManipulation(value = value)
        }
    }

    suspend fun removeCartItem(data: SneakerData) {
        withContext(Dispatchers.IO) {
            getCartItems = mViewModel.removeCartItem(data.id)
            withContext(Dispatchers.Main) {
                isShowProgress.value = true
                getCartItems?.observe(viewLifecycleOwner, removeCartObserver)
            }
        }
    }

    private val removeCartObserver = object : Observer<List<SneakerData>?> {
        override fun onChanged(value: List<SneakerData>?) {
            if (value != null && value.size > 0) {
                getCartItems?.removeObserver(this)
                isCartEmpty.value = false
                mAdapter.submitList(value)
                if (positionRemoved != -1) {
                    mAdapter.notifyItemRemoved(positionRemoved)
                }
            } else {
                isCartEmpty.value = true
            }
            updateManipulation(value = value)

        }
    }

    fun updateManipulation(value: List<SneakerData>?) {
        if (value != null) {
            cartItemList.clear()
            cartItemList.addAll(value)
        } else {
            cartItemList.clear()
        }
        lifecycleScope.launch {
            handleAmountChanges(cartItemList)
        }

    }

    suspend fun handleAmountChanges(value: List<SneakerData>?) {
        withContext(Dispatchers.Default) {
            var subTotal = 0
            value?.forEach {
                subTotal = subTotal + it.retailPrice
            }
            withContext(Dispatchers.Main) {
                isShowProgress.value = false
                subtotalAmount.value = "$" + subTotal
                totalAmount.value = "$" + Math.addExact(subTotal, 40)
            }
        }
    }
}