package com.example.sneakersapp.fragment

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.Navigation
import com.example.sneakersapp.R
import com.example.sneakersapp.adapter.HomeAdapter
import com.example.sneakersapp.data.SneakerData
import com.example.sneakersapp.data.SneakerID
import com.example.sneakersapp.databinding.LayoutHomeFragmentBinding
import com.example.sneakersapp.network.Resource
import com.example.sneakersapp.network.Status
import com.example.sneakersapp.util.AutoClearedValue
import com.example.sneakersapp.util.HOME_ADAPTER_ADD_TO_CART
import com.example.sneakersapp.util.HOME_ADAPTER_ITEM_SELECTED
import com.example.sneakersapp.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {
    var mBinding: AutoClearedValue<LayoutHomeFragmentBinding>? = null  //to prevent memory leaks

    lateinit var mMainAdapter: HomeAdapter

    lateinit var mViewModel: HomeViewModel

    private var isShowProgress = MutableLiveData<Boolean>()

    var getSneakerData: LiveData<Resource<List<SneakerData>>>? = null

    var dataList: MutableList<SneakerData> = ArrayList()

    val list = mutableListOf<String>("Price", "Popular")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        DataBindingUtil.inflate<LayoutHomeFragmentBinding>(
            inflater, R.layout.layout_home_fragment, container, false
        ).also {
            it.lifecycleOwner = viewLifecycleOwner
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

        setAdapter()
        setupCategorySelectionSpinner()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { //enabling back in toolbar
            (it as AppCompatActivity).setSupportActionBar(mBinding?.get()?.toolbar)
            it.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            it.supportActionBar?.setDisplayShowHomeEnabled(false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard, menu)
        val searchItem = menu.findItem(R.id.search)
        val searchView: SearchView? = searchItem.actionView as SearchView?
        searchView?.queryHint = getString(R.string.search_by_title)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    fun setAdapter() {
        if (!::mMainAdapter.isInitialized) {
            mMainAdapter = HomeAdapter(mContext = context, mClickCallback = this::onItemSelected)
        }
        mBinding?.get()?.adapter = mMainAdapter
        getData()
    }

    fun getData() {
        getSneakerData = mViewModel.getSneakerData(force = false)
        getSneakerData?.observe(viewLifecycleOwner, getDataObserver)
    }

    private val getDataObserver = object : Observer<Resource<List<SneakerData>>> {
        override fun onChanged(value: Resource<List<SneakerData>>) {
            if (value.status == Status.SUCCESS) {
                value.data?.let {
                    dataList.clear()
                    dataList.addAll(it)
                    mMainAdapter.submitList(it)
                }
            } else if (value.status == Status.ERROR) {
                if (value.code == 400) {
                    Toast.makeText(
                        context, "Invalid query parameter supplied", Toast.LENGTH_SHORT
                    ).show()
                } else if (value.code == 500) {
                    Toast.makeText(context, "Unexpected error", Toast.LENGTH_SHORT).show()
                }
                value.data?.let {
                    mMainAdapter.submitList(it)
                    dataList.clear()
                    dataList.addAll(it)
                }
            }
        }
    }

    fun setupCategorySelectionSpinner() {
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(), R.layout.event_spinner_item, ArrayList(list)
        )
        adapter.setDropDownViewResource(R.layout.event_spinner_item)

        mBinding?.get()?.sortBySpinner?.setAdapter(adapter)

        mBinding?.get()?.sortBySpinner?.setSelection(0)

        mBinding?.get()?.sortBySpinner?.onItemSelectedListener = itemSelectedListener
    }

    private val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            //coming soon
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
            //Coming soon
        }
    }

    fun onItemSelected(action: Int, data: SneakerData?) {
        when (action) {
            HOME_ADAPTER_ITEM_SELECTED -> {
                data?.let {
                    navigateToDetailFragment(it)
                } ?: Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show()
            }

            HOME_ADAPTER_ADD_TO_CART -> {
                data?.let {
                    isShowProgress.value = true
                    lifecycleScope.launch {
                        insertItemToCart(it)
                    }
                } ?: Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun navigateToDetailFragment(data: SneakerData) { //navigating to detail view with only photos
        view?.let {
            Navigation.findNavController(it).navigate(
                HomeFragmentDirections.showSneakerDetailFragment(data)
            )
        }
    }

    suspend fun insertItemToCart(data: SneakerData) {
        withContext(Dispatchers.IO) {
            if (data.id != null && data.id.isNotEmpty()) {
                mViewModel.insertSneakerDataIntoCart(SneakerID(data.id))

                withContext(Dispatchers.Main) {
                    isShowProgress.value = false
                    Toast.makeText(
                        context, getString(R.string.item_added_to_cart), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    fun filterList(text: String) {
        isShowProgress.value = true
        val filterList: MutableList<SneakerData> = ArrayList()
        dataList?.forEach {
            if (it.title?.lowercase()?.contains(text) ?: false) {
                filterList.add(it)
            }
        }

        if (filterList.isEmpty()) {
            isShowProgress.value = false
            Toast.makeText(context, getString(R.string.no_item_found), Toast.LENGTH_SHORT).show()
        } else {
            isShowProgress.value = false
            mMainAdapter.submitList(filterList)
        }
    }
}