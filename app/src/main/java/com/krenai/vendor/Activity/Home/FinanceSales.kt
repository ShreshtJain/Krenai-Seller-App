
package com.krenai.vendor.Activity.Home

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
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
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.material.datepicker.MaterialDatePicker
import com.krenai.vendor.Adapter.FinanceSalesAdapter
import com.krenai.vendor.Adapter.SalesByProductAdapter
import com.krenai.vendor.Adapter.SalesOverTimeAdapter
import com.krenai.vendor.R
import com.krenai.vendor.model.DataFinanceSales
import com.krenai.vendor.model.DataSalesByProduct
import com.krenai.vendor.model.DataSalesOverTime
import com.krenai.vendor.utils.jkeys.Keys.CommonResources.CONNECTION_URL_DEMAND
import com.krenai.vendor.utils.jkeys.Keys.Home.DATA_PATH_FINANCE_SALES_REPORT
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class FinanceSales : AppCompatActivity() {

    lateinit var clear: Button
    private lateinit var date: TextView
    private lateinit var date_s: String
    lateinit var dayformat: SimpleDateFormat
    lateinit var monthformat: SimpleDateFormat
    lateinit var yearformat: SimpleDateFormat
    lateinit var weekformat: SimpleDateFormat
    lateinit var dateFormat: SimpleDateFormat
    lateinit var daystring:String
    lateinit var monthstring:String
    lateinit var yearstring:String
    lateinit var daystring2:String
    lateinit var monthstring2:String
    lateinit var yearstring2:String
    lateinit var weekstring:String
    lateinit var weekstring2:String
    lateinit var recycler: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    var url=CONNECTION_URL_DEMAND+DATA_PATH_FINANCE_SALES_REPORT+"?sortBy=createdDate&sortOrder=Desc&"
    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""
    lateinit var result_text: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_sales)
        recycler=findViewById(R.id.sales_recycler)
        result_text=findViewById(R.id.result_text)
        date=findViewById(R.id.date)
        dateFormat = SimpleDateFormat("dd MMM yyyy ")
        dayformat=SimpleDateFormat("dd")
        monthformat=SimpleDateFormat("MMM")
        yearformat= SimpleDateFormat("yyyy")
        weekformat= SimpleDateFormat("EEE")

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        daystring2=dayformat.format(calendar.getTime())
        monthstring2=monthformat.format(calendar.getTime())
        yearstring2=yearformat.format(calendar.getTime())
        weekstring2=weekformat.format(calendar.getTime())

        val calendar2=Calendar.getInstance()
        calendar2.add(Calendar.MONTH, -1)
        calendar2.set(Calendar.DAY_OF_MONTH, 1)

        daystring=dayformat.format(calendar2.getTime())
        monthstring=monthformat.format(calendar2.getTime())
        yearstring=yearformat.format(calendar2.getTime())
        weekstring=weekformat.format(calendar2.getTime())

        date_s = dateFormat.format(calendar.getTime())

        date.text=dateFormat.format(calendar2.getTime())+" - "+date_s

        clear=findViewById(R.id.clear)
        sharedPreferencesStatus = getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")
        layoutManager = LinearLayoutManager(this)
        recycler.addItemDecoration(
            DividerItemDecoration
                (
                recycler.context,
                (layoutManager as LinearLayoutManager).orientation
            )
        )
        clear.setOnClickListener {
            finish()
        }
        date.setOnClickListener {
            val builder = MaterialDatePicker.Builder.dateRangePicker()
            val now = Calendar.getInstance()
            builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
            val picker = builder.build()
            picker.show(supportFragmentManager, picker.toString())
            //picker.addOnNegativeButtonClickListener { dismiss() }
            picker.addOnPositiveButtonClickListener {
                val first=Date((it.first) as Long)
                val second= Date((it.second) as Long)
                val f=dateFormat.format(first)
                val s=dateFormat.format(second)
                daystring=dayformat.format(first)
                weekstring=weekformat.format(first)
                monthstring=monthformat.format(first)
                yearstring=yearformat.format(first)
                daystring2=dayformat.format(second)
                monthstring2=monthformat.format(second)
                yearstring2=yearformat.format(second)
                weekstring2=weekformat.format(second)
                date.setText(f+" - "+s)
                setupRecycler()
            }
        }
        setupRecycler()
    }
    fun setupRecycler()
    {

        val list= arrayListOf<DataFinanceSales>()
        val queue = Volley.newRequestQueue(this)
        val startdate="startDate="+weekstring+"%20"+monthstring+"%20"+daystring+"%20"+yearstring
        val enddate="%2000:00:00&endDate="+weekstring2+"%20"+monthstring2+"%20"+daystring2+"%20"+yearstring2+"%2023:59:59"
        val url1=url+startdate+enddate+"&stateId=6&supplierId="+userid?.toDouble()?.toInt().toString()
        Log.i("url1",url1)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,url1,
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    val data = response.getJSONArray("object")
                    for (i in 0 until data.length())
                    {
                        val Object = data.getJSONObject(i)
                        val model= DataFinanceSales(Object.getString("date"),Object.getString("productName"),Object.getString("orderCount"),
                            Object.getString("grossSale"),Object.getString("discount"),Object.getString("returns"),
                            Object.getString("netSale"),Object.getString("shippingCharges"),Object.getString("tax"),
                            Object.getString("totalSale"))
                        list.add(model)
                    }


                    result_text.setText("Total Result - "+list.size)
                    val Adapter =
                        FinanceSalesAdapter(this, list)
                    val mLayoutManager = LinearLayoutManager(this)
                    recycler.layoutManager = mLayoutManager
                    recycler.adapter = Adapter
                    recycler.setHasFixedSize(true)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError? ->
                // Toast.makeText(activity as Context, "error", Toast.LENGTH_SHORT).show()
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
