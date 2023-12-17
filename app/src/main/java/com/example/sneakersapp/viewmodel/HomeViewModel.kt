package com.example.sneakersapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.sneakersapp.data.SneakerData
import com.example.sneakersapp.data.SneakerID
import com.example.sneakersapp.database.db.SneakerDb
import com.example.sneakersapp.network.Resource
import com.example.sneakersapp.repository.HomeRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var homeRepository: HomeRepository

    init {
        val mHomeDao = SneakerDb.getDatabase(application).getHomeDao()
        homeRepository = HomeRepository(mHomeDao = mHomeDao)
    }

    fun getSneakerData(force: Boolean): LiveData<Resource<List<SneakerData>>> {
        return homeRepository.getSneakerData(force = force)
    }

    fun insertSneakerDataIntoCart(sneakerID: SneakerID) {
        homeRepository.insertItemToCart(sneakerID)
    }

    fun getSneakerCartData(): LiveData<List<SneakerData>> {
        return homeRepository.getCartItems()
    }

    fun removeCartItem(sneakerID: String) : LiveData<List<SneakerData>> {
        return homeRepository.removeCartItem(sneakerID)
    }
}