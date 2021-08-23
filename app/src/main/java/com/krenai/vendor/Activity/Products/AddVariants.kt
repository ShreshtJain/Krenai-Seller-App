package com.krenai.vendor.Activity.Products

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.krenai.vendor.Adapter.VariantAdapter
import com.krenai.vendor.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class AddVariants : AppCompatActivity() {
    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""
    lateinit var variantsRecycler: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var clear: Button
    lateinit var save:TextView
    lateinit var variantOptions: JSONArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_variants)
        sharedPreferencesStatus = getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")
        variantsRecycler=findViewById(R.id.varaint_recycler)
        clear=findViewById(R.id.clear)
        save=findViewById(R.id.save)
        variantOptions= JSONArray(getIntent().getStringExtra("variantOptions"))
        val editor = sharedPreferencesStatus!!.edit()
        editor.putString("sharedVariantOptions",variantOptions.toString())
        editor.apply()

        save.setOnClickListener {
            val sharedVariantOptions=sharedPreferencesStatus?.getString("sharedVariantOptions","")
            val jsonVariantOptions=JSONArray(sharedVariantOptions)
            val jsonVariantOptionsFinal=JSONArray()
            var index=0
            for (i in 0 until jsonVariantOptions.length())
            {
                if(!(jsonVariantOptions.getJSONObject(i).getJSONArray("variantValue").length()==0))
                {
                    jsonVariantOptionsFinal.put(index,jsonVariantOptions.getJSONObject(i))
                    index+=1
                }
            }
            Log.i("variantOptions",jsonVariantOptionsFinal.toString())
            var intent= Intent(this,ProductUpdate::class.java)
            intent.putExtra("variantOptions",jsonVariantOptionsFinal.toString())
            intent.putExtra("starter","starter")
            startActivity(intent)
            finish()
        }
        clear.setOnClickListener {
            finish()
        }
        layoutManager = LinearLayoutManager(this)
        variantsRecycler.addItemDecoration(
            DividerItemDecoration    (
                variantsRecycler.context,
                (layoutManager as LinearLayoutManager).orientation
            )
        )
        setUpRecycler()
    }
    fun setUpRecycler()
    {
        val queue = Volley.newRequestQueue(this)
        val listing= arrayListOf<String>()
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, "https://www.outsourcecto.com/api/v3/stores/variant/all",
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    val data = response.getJSONArray("object")
                    for (i in 0 until data.length()) {
                        val Object = data.getJSONObject(i)
                    listing.add(Object.getString("name"))
                    }
                    val Adapter = VariantAdapter(this,listing,variantOptions,1)
                    val mLayoutManager = LinearLayoutManager(this)
                    variantsRecycler.layoutManager = mLayoutManager
                    variantsRecycler.adapter = Adapter
                    variantsRecycler.setHasFixedSize(true)
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
