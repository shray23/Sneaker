package com.example.sneakersapp.typeconverter

import androidx.room.TypeConverter
import com.example.sneakersapp.data.SneakerID
import com.example.sneakersapp.data.SneakerMedia
import com.google.gson.Gson

class HomeTypeConverter {

    @TypeConverter
    fun objectToJsonDriveDetailViewData(value: SneakerMedia?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToObjectDriveDetailViewData(value: String?) = Gson().fromJson(value, SneakerMedia::class.java)

    @TypeConverter
    fun listToJsonStrings(value: MutableList<String>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToListString(value: String) = Gson().fromJson(value, Array<String>::class.java).toMutableList()

    @TypeConverter
    fun objectToJsonSneakerIdData(value: SneakerID?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToObjectSneakerIdData(value: String?) = Gson().fromJson(value, SneakerID::class.java)
}