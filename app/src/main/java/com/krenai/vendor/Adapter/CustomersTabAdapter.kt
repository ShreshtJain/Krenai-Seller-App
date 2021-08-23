package com.krenai.vendor.Adapter

import android.content.Context;
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.krenai.vendor.Activity.Home.CustomerDetails
import com.krenai.vendor.Fragment.*
import org.json.JSONArray
import org.json.JSONObject

class CustomersTabAdapter(private val myContext: Context, fm: FragmentManager, internal var totalTabs: Int,val jsonObject: JSONObject,val cartUrl:String,
var promoWalletUrl:String,val mainWalletUrl:String,val creditBookUrl:String) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                //  val homeFragment: HomeFragment = HomeFragment()
                return customerAddressFragment(jsonObject)
            }
            1 -> {
                return customerOrderFragment(cartUrl)
            }
            2 -> {
                // val movieFragment = MovieFragment()
                return customerWalletFragment(mainWalletUrl)
            }
            3 -> {
                return customerPromoFragment(promoWalletUrl)
            }
            4 -> {
                // val movieFragment = MovieFragment()
                return customerCreditFragment(creditBookUrl)
            }
            else -> return customerAddressFragment(jsonObject)
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}