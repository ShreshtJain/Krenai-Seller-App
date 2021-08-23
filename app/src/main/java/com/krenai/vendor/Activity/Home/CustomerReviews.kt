package com.krenai.vendor.Activity.Home

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.krenai.vendor.Adapter.ReviewAdapter
import com.krenai.vendor.R
import com.krenai.vendor.model.ReviewModel
import com.krenai.vendor.utils.jkeys.Keys.CommonResources.CONNECTION_URL_DEMAND
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class CustomerReviews : AppCompatActivity() {

    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid: String? = ""
    private var token: String? = ""

    lateinit var recyclerView: RecyclerView
    var adapter:ReviewAdapter?=null

    lateinit var pb:ProgressBar
    lateinit var noData:LinearLayout

    lateinit var clear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_reviews)

        sharedPreferencesStatus = getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid = sharedPreferencesStatus?.getString("USER_ID", "")
        token = sharedPreferencesStatus?.getString("TOKEN", "")

        recyclerView=findViewById(R.id.reviews_recycler)

        noData=findViewById(R.id.no_data)
        pb=findViewById(R.id.pb)

        clear=findViewById(R.id.clear)
        clear.setOnClickListener {
            finish()
        }

        setUpRecycler()
    }

    private fun setUpRecycler()
    {
        val url=CONNECTION_URL_DEMAND+"api/v3/store/order/review/supplier/"+userid?.toDouble()?.toInt().toString()+"?ratings="
        val arrayList= arrayListOf<ReviewModel>()

        Log.i("url",url)

        noData.visibility= View.GONE
        pb.visibility= View.VISIBLE
        recyclerView.visibility=View.GONE

        val queue = Volley.newRequestQueue(this)

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url,
            Response.Listener<JSONObject>() { response ->
                pb.visibility= View.GONE
                recyclerView.visibility=View.VISIBLE

                try {

                    val data = response.getJSONArray("object")
                    if(data.length()<=0)
                    {
                        noData.visibility= View.VISIBLE
                    }
                    for (i in 0 until data.length()) {

                        val Object=data.getJSONObject(i)
                        val customerObject=Object.getJSONObject("customerDetail")

                        val createdDate=(Object.getString("createdDate"))

                        val date= createdDate.subSequence(0,10).toString()
                        var time=createdDate.subSequence(11,19).toString()
                        if(time.subSequence(0,2).toString().toInt()>12)
                        {
                            var t=(time.subSequence(0,2).toString().toInt()-12).toString()
                            if(t.length<2)
                            {
                                t="0"+t
                            }
                            time=t+time.subSequence(2,8).toString()+" PM"
                        }
                        else
                        {
                            time=time+" AM"
                        }
                        arrayList.add(ReviewModel(customerObject.getString("profilePicUrl"),Object.getString("rating"),
                        date+" | "+time,customerObject.getString("firstName"),Object.getString("review")))

                    }
                   adapter =
                        ReviewAdapter(this, arrayList)
                    val mLayoutManager = LinearLayoutManager(this)
                    recyclerView.layoutManager = mLayoutManager
                    recyclerView.adapter = adapter
                    recyclerView.setHasFixedSize(true)


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError? ->
                Toast.makeText(this,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                pb.visibility= View.GONE
                recyclerView.visibility=View.VISIBLE
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers =
                    HashMap<String, String>()
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                headers["Authorization"] = " Bearer "+token

                return headers
            }
        }
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                50000,
        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue.add(jsonObjectRequest)
    }
}
