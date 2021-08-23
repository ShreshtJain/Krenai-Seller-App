package com.krenai.vendor.Fragment


import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.krenai.vendor.Adapter.CreditAdapter
import com.krenai.vendor.Adapter.OrderHistoryAdapter

import com.krenai.vendor.R
import com.krenai.vendor.model.DataCreditModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

/**
 * A simple [Fragment] subclass.
 */
class customerOrderFragment(val cartUrl: String) : Fragment() {

    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid: String? = ""
    private var token: String? = ""

    lateinit var recyclerView: RecyclerView
    lateinit var pb: ProgressBar
    lateinit var noData: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val View=inflater.inflate(R.layout.fragment_customer_order, container, false)

        recyclerView=View.findViewById(R.id.recycler)
        pb=View.findViewById(R.id.pb)

        noData=View.findViewById(R.id.no_data)

        sharedPreferencesStatus = activity?.getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid = sharedPreferencesStatus?.getString("USER_ID", "")
        token = sharedPreferencesStatus?.getString("TOKEN", "")

        onAddOrderHistory()

        return View
    }

    private fun onAddOrderHistory() {

        noData.visibility=View.GONE
        pb.visibility=View.VISIBLE

        val arrayList= arrayListOf<DataCreditModel>()
        val queue = Volley.newRequestQueue(activity as Context)

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, cartUrl,
            Response.Listener<JSONObject>() { response ->
                pb.visibility=View.GONE
                try {
                    val data = response.getJSONArray("object")
                    val cartObject=data.getJSONObject(0)
                    try {
                        val cart = cartObject.getJSONArray("cart")

                        if (cart.length() <= 0) {
                            noData.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        } else {
                            recyclerView.visibility = View.VISIBLE
                            noData.visibility = View.GONE

                            for (i in 0 until cart.length()) {

                                val Object = cart.getJSONObject(i)

                                arrayList.add(
                                    DataCreditModel(
                                        Object.getString("id"),
                                        Object.getJSONObject("state").getString("description"),
                                        Object.getString("createdDate"),
                                        Object.getString("orderAmount"),
                                        Object.getString("paymentMode")
                                    )
                                )

                            }
                            val adapter =
                                OrderHistoryAdapter(activity as Context, arrayList)
                            val mLayoutManager = LinearLayoutManager(activity as Context)
                            recyclerView.layoutManager = mLayoutManager
                            recyclerView.adapter = adapter
                            recyclerView.setHasFixedSize(true)
                        }
                    }
                    catch(e:JSONException)
                    {
                        noData.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError? ->
                pb.visibility=View.GONE
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
