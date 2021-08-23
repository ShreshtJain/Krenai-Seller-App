package com.krenai.vendor.Fragment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.krenai.vendor.Adapter.CustomerAddressAdapter
import com.krenai.vendor.Adapter.ReviewAdapter

import com.krenai.vendor.R
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class customerAddressFragment(val jsonObject: JSONObject) : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var noData:LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val View=inflater.inflate(R.layout.fragment_customer_address, container, false)

        recyclerView=View.findViewById(R.id.recycler)
        noData=View.findViewById(R.id.no_data)

        onAddAddress()

        return View
    }

    private fun onAddAddress() {

        val addressJsonArray=jsonObject.getJSONArray("customerAddresses")
        val arrayList= arrayListOf<JSONObject>()

        if(addressJsonArray.length()<=0)
        {
            noData.visibility=View.VISIBLE
            recyclerView.visibility=View.GONE
        }
        else {
            noData.visibility=View.GONE
            recyclerView.visibility=View.VISIBLE
            for (i in 0 until addressJsonArray.length()) {
                arrayList.add(addressJsonArray.getJSONObject(i))

            }
           val adapter =
               CustomerAddressAdapter(activity as Context, arrayList)
            val mLayoutManager = LinearLayoutManager(activity as Context)
            recyclerView.layoutManager = mLayoutManager
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(true)
        }
    }
}
