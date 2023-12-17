package com.example.sneakersapp.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.sneakersapp.R
import com.example.sneakersapp.data.SneakerData
import com.example.sneakersapp.database.db.SneakerDb
import com.example.sneakersapp.util.ReadJSONFromAssets
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    lateinit var bottomNavigationView : BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handleData()
        setBottomNav()
    }


    fun handleData() {
        lifecycleScope.launch {
            fetchAndStoreData()
        }
    }

    suspend fun fetchAndStoreData() {
        withContext(Dispatchers.Default) {
            val mHomeDao = SneakerDb.getDatabase(application).getHomeDao()
            val gson = Gson()
            val jsonData = ReadJSONFromAssets(this@MainActivity, "SneakerData.json")
            val allSneakerData: List<SneakerData> =
                gson.fromJson(jsonData, Array<SneakerData>::class.java).asList()
            mHomeDao.deleteAllData()  //deleting existing data
            mHomeDao.insertSneakerData(allSneakerData)
        }
    }

    fun setBottomNav() {
        bottomNavigationView = findViewById(R.id.mainBottomNavigation)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainNavFragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView
            .setupWithNavController(navController)

        bottomNavigationView.itemIconTintList = null

        bottomNavigationView.setOnItemSelectedListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.homeFragment -> navController.navigate(R.id.homeFragment)
                R.id.cartFragment -> navController.navigate(R.id.cartFragment)
                else -> onNavDestinationSelected(menuItem, navController)
            }
            true
        }


    }

}