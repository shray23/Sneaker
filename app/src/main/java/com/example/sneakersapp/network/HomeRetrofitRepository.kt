package com.example.sneakersapp.network

import androidx.room.Room
import com.example.sneakersapp.database.db.SneakerDb
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class HomeRetrofitRepository {



    companion object {
        private var instance: HomeRetrofitRepository? = null
        var apiService: ApiService? = null

        fun getInstance(): HomeRetrofitRepository {
            return instance ?: synchronized(this) {
                val inst = HomeRetrofitRepository()
                instance = inst
                inst
            }
        }
    }

}