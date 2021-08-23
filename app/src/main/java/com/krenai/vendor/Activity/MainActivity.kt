package com.krenai.vendor.Activity;

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.krenai.vendor.Fragment.Dashboard
import com.krenai.vendor.Fragment.Products
import com.krenai.vendor.Fragment.Orders
import com.krenai.vendor.Fragment.Profile
import com.krenai.vendor.R
import com.krenai.vendor.utils.Firebase.MyFirebaseMessagingService


class MainActivity(): AppCompatActivity(), View.OnClickListener {

    var currentFragment: Fragment? = null
    var ft: FragmentTransaction? = null
    private var onRecentBackPressedTime: Long = 0

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    currentFragment = Dashboard()
                    ft = supportFragmentManager.beginTransaction()
                    ft!!.replace(R.id.frame_layout, currentFragment!!)
                    ft!!.commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_orders -> {
                    currentFragment = Orders()
                    ft = supportFragmentManager.beginTransaction()
                    ft!!.replace(R.id.frame_layout, currentFragment!!)
                    ft!!.commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_products -> {
                    currentFragment = Products()
                    ft = supportFragmentManager.beginTransaction()
                    ft!!.replace(R.id.frame_layout, currentFragment!!)
                    ft!!.commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    currentFragment = Profile()
                    ft = supportFragmentManager.beginTransaction()
                    ft!!.replace(R.id.frame_layout, currentFragment!!)
                    ft!!.commit()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

        override fun onCreate(savedInstanceState: Bundle?) {

            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            currentFragment = Dashboard()
            ft = supportFragmentManager.beginTransaction()
            ft!!.replace(R.id.frame_layout, currentFragment!!)
            ft!!.commit()

            val navigation = findViewById<View>(R.id.navigation) as BottomNavigationView
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

            clearNotification()
        }
    override fun onBackPressed() {
        if (System.currentTimeMillis() - onRecentBackPressedTime > 2000) {
            onRecentBackPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show()
            return
        }
        super.onBackPressed()
    }

    override fun onClick(view: View) {

    }
    fun clearNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(0)
    }
}

