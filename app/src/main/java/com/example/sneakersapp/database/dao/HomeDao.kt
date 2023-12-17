package com.example.sneakersapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sneakersapp.data.SneakerData
import com.example.sneakersapp.data.SneakerID

@Dao
interface HomeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSneakerData(item: SneakerData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSneakerData(item: List<SneakerData>)

    @Query("select * from SneakerData ORDER BY releaseDate DESC")
    fun getAllSneakerData(): LiveData<List<SneakerData>>

    @Query("DELETE FROM SneakerData")
    fun deleteAllData()


    //cart related
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItemToCart(item: SneakerID)

    @Query("select * from SneakerData where id in (select sneakerId from SneakerID)")
    fun getSneakerCartData() : LiveData<List<SneakerData>>

    @Query("DELETE FROM SneakerID where sneakerId=:id")
    fun deleteSneakerFromCartById(id: String)
}