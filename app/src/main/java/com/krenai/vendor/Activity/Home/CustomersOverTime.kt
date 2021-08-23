package com.krenai.vendor.Activity.Home

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.shimmer.ShimmerFrameLayout
import com.krenai.vendor.Adapter.CustomerOverTimeAdapter
import com.krenai.vendor.Adapter.OrdersListingAdapter
import com.krenai.vendor.R
import com.krenai.vendor.model.Dataorders
import com.krenai.vendor.utils.jkeys.Keys.CommonResources.CONNECTION_URL
import com.krenai.vendor.utils.jkeys.Keys.CommonResources.CONNECTION_URL_DEMAND
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class CustomersOverTime : AppCompatActivity() {

    lateinit var customerRecycler:RecyclerView
    lateinit var shimmerFrameLayout:ShimmerFrameLayout
    lateinit var clear:Button
    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""
    private var dataordersList = arrayListOf<Dataorders>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customers_over_time)

        customerRecycler=findViewById(R.id.Customers_over_time_recycler)
        clear=findViewById(R.id.clear)

        shimmerFrameLayout=findViewById(R.id.shimmerLayout)

        sharedPreferencesStatus = getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")

        setUpRecycler()

        clear.setOnClickListener {
            finish()
        }
    }
    fun setUpRecycler()
    {
        val url=CONNECTION_URL + "api/v3/customers/supplier/"+userid?.toDouble()?.toInt().toString()
        Log.i("url",url)

        shimmerFrameLayout.setVisibility(View.VISIBLE)
        shimmerFrameLayout.startShimmer()
        customerRecycler.setVisibility(View.GONE)

        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url,
            Response.Listener<JSONObject>() { response ->

                customerRecycler.setVisibility(View.VISIBLE)
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.setVisibility(View.GONE)
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    val data = response.getJSONArray("object")

                    for (i in 0 until data.length()) {
                        val OrderObject = data.getJSONObject(i)

                            val OrdersModel = Dataorders(
                                OrderObject.getString("firstName"),
                                String.format("%.2f",(OrderObject.getString("totalOrderAmount")).toDouble()),
                                OrderObject.getString("phoneNo"),
                                OrderObject.getString("emailId"),
                                OrderObject.getJSONObject("state").getString("description"),
                                "",
                                OrderObject.getString("createdDate"),
                                OrderObject.getString("updatedDate"),
                                "",
                                i,OrderObject
                            )
                            dataordersList.add(OrdersModel)

                    }

                        val adapter =
                           CustomerOverTimeAdapter(this, dataordersList)
                        val mLayoutManager = LinearLayoutManager(this)
                        customerRecycler.layoutManager = mLayoutManager
                        customerRecycler.adapter = adapter
                        customerRecycler.setHasFixedSize(true)
                    //}
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError? ->
                //Toast.makeText(activity as Context, "error", Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers =
                    HashMap<String, String>()
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                headers["Authorization"] = " Bearer "+token

                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }
}
