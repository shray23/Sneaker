package com.example.sneakersapp.repository

import androidx.lifecycle.LiveData
import com.example.sneakersapp.data.SneakerData
import com.example.sneakersapp.data.SneakerID
import com.example.sneakersapp.database.dao.HomeDao
import com.example.sneakersapp.network.*
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class HomeRepository constructor(val mHomeDao: HomeDao) {

    var mApiService: ApiService
    var mAppExecutors : AppExecutors
    val header by lazy {
        HashMap<String, String>()
    }

    init {
        mAppExecutors = AppExecutors()
        mApiService = Retrofit.Builder()
            .baseUrl("https://www.google.com/")
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(ApiService::class.java)
    }

    fun getSneakerData(force: Boolean): LiveData<Resource<List<SneakerData>>> {
        return object : NetworkBoundResource<List<SneakerData>, List<SneakerData>>(mAppExecutors) {

            override fun saveCallResult(item: List<SneakerData>) {
                item.let {
                    if (it.size > 0) {
                        mHomeDao.deleteAllData()  //deleting existing data
                        mHomeDao.insertSneakerData(it) //adding new data in db
                    }
                }

            }

            override fun shouldFetch(data: List<SneakerData>?): Boolean {
                return force  //currently false
            }

            override fun loadFromDb(): LiveData<List<SneakerData>> {
                return mHomeDao.getAllSneakerData()
            }

            override fun createCall(): LiveData<ApiResponse<List<SneakerData>>> {
                header.put("auth", "unique auth")
                return mApiService.getAllSneakerData(header)
            }

            override fun onFetchFailed(response: ApiResponse<List<SneakerData>>?) {
                super.onFetchFailed(response)

            }
        }.asLiveData()
    }

    fun insertItemToCart(sneakerID: SneakerID) {
     mHomeDao.insertItemToCart(item = sneakerID)
    }

    fun getCartItems() : LiveData<List<SneakerData>> {
        return mHomeDao.getSneakerCartData()
    }

    fun removeCartItem(sneakerID: String) : LiveData<List<SneakerData>> {
        mHomeDao.deleteSneakerFromCartById(sneakerID)
        return mHomeDao.getSneakerCartData()
    }
}