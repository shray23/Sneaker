package com.example.sneakersapp.database.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.sneakersapp.data.SneakerData
import com.example.sneakersapp.data.SneakerID
import com.example.sneakersapp.database.dao.HomeDao
import com.example.sneakersapp.typeconverter.HomeTypeConverter

@Database(entities = [SneakerData::class, SneakerID::class], version = 1, exportSchema = false)
@TypeConverters(HomeTypeConverter::class)
abstract class SneakerDb : RoomDatabase() {

    abstract fun getHomeDao(): HomeDao

    companion object {
        @Volatile
        private var INSTANCE: SneakerDb? = null

        fun getDatabase(context: Context): SneakerDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SneakerDb::class.java,
                    "sneaker_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}