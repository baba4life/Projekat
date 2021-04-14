package com.example.nilme

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.nilme.fragments.HomeFragment
import com.example.nilme.fragments.NotificationsFragment
import com.example.nilme.fragments.ProfileFragment
import com.example.nilme.fragments.SearchFragment

class MainActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                moveToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_add -> {
               return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                moveToFragment(NotificationsFragment())
                return@OnNavigationItemSelectedListener true

            }
            R.id.navigation_profile -> {
                moveToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true

            }

        }

        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        moveToFragment(HomeFragment())  

    }
    private fun moveToFragment(fragment : Fragment){
        var fragmentTransision =supportFragmentManager.beginTransaction()
         fragmentTransision.replace(R.id.fragment_container,fragment)
        fragmentTransision.commit()
    }
}