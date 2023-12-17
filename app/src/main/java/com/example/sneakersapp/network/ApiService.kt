package com.example.sneakersapp.network

import androidx.lifecycle.LiveData
import com.example.sneakersapp.data.SneakerData
import retrofit2.http.GET
import retrofit2.http.HeaderMap

interface ApiService {

    @GET("api")
    fun getAllSneakerData(
        @HeaderMap headerMap: HashMap<String, String>
    ): LiveData<ApiResponse<List<SneakerData>>>
}