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
import com.krenai.vendor.Adapter.SalesOverTimeAdapter
import com.krenai.vendor.R
import com.krenai.vendor.model.DataSalesOverTime
import com.krenai.vendor.utils.jkeys.Keys.CommonResources.CONNECTION_URL
import com.krenai.vendor.utils.jkeys.Keys.Home.DATA_PATH_SALES_OVER_TIME_REPORT
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class SalesOverTime : AppCompatActivity() {
    lateinit var barchart: BarChart

    lateinit var clear: Button

    private lateinit var date: TextView
    private lateinit var date_s: String
    lateinit var dayformat:SimpleDateFormat
    lateinit var monthformat:SimpleDateFormat
    lateinit var yearformat:SimpleDateFormat
    lateinit var weekformat:SimpleDateFormat
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

    var url=CONNECTION_URL+DATA_PATH_SALES_OVER_TIME_REPORT+"?"

    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""

    lateinit var result_text:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sales_over_time)

        result_text=findViewById(R.id.result_text)

        barchart = findViewById(R.id.barchart) as BarChart

        clear=findViewById(R.id.clear)

        date=findViewById(R.id.date)

        recycler=findViewById(R.id.sales_over_time_recycler)

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
        val arr= arrayOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
        val datelist= arrayListOf<String>()
        val bar_list= arrayListOf<Float>()
        val list= arrayListOf<DataSalesOverTime>()
        val queue = Volley.newRequestQueue(this)
        val startdate="startDate="+weekstring+"%20"+monthstring+"%20"+daystring+"%20"+yearstring
        val enddate="%2000:00:00&endDate="+weekstring2+"%20"+monthstring2+"%20"+daystring2+"%20"+yearstring2+"%2023:59:59"
        val url1=url+startdate+enddate+"&supplierId="+userid?.toDouble()?.toInt().toString()
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
                        val model=DataSalesOverTime(Object.getString("date"),Object.getString("orderCount"),
                            Object.getString("grossSales"),Object.getString("couponDiscount"),Object.getString("returns"),
                            Object.getString("netSales"),Object.getString("shippingCharges"),Object.getString("tax"),
                            Object.getString("totalSales"))
                        bar_list.add(Object.getString("totalSales").toFloat())
                        datelist.add(Object.getString("date"))
                        list.add(model)
                    }
                    val entries1 = arrayListOf<BarEntry>()
                    val labels= arrayListOf<String>()
                    for (i in 0 until datelist.size)
                    {
                        val s1=datelist[i].subSequence(5,7).toString().toInt()
                        val s2=datelist[i].subSequence(8,10).toString()
                        val s3=arr[s1-1]
                        labels.add(s3+" "+s2)
                    }
                    Log.i("labels",labels.toString())
                    Log.i("bar",bar_list.toString())
                    barchart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                    for (i in 0 until bar_list.size)
                    {
                        entries1.add(BarEntry((i).toFloat(),bar_list[i]))
                    }
                    Log.i("entries",entries1.toString())
                    barchart.setTouchEnabled(true);
                    barchart.setDragEnabled(true);
                    barchart.setScaleEnabled(true);
                    barchart.animateXY(3000, 3000);
                    barchart.setHorizontalScrollBarEnabled(true);
                    barchart.setDoubleTapToZoomEnabled(true)
                    val dataSets = arrayListOf<IBarDataSet>()

                    val barDataSet1 = BarDataSet(entries1, "SALES")
                    barDataSet1.setColors(
                        resources.getColor(R.color.quantum_orange)
                    )
                    barDataSet1.setValueTextColor(Color.WHITE)
                    barchart.getAxisLeft().setDrawGridLines(false)
                    barchart.getXAxis().setDrawGridLines(false)
                    // barchart.getAxisRight().setEnabled(false)
                    barchart.getAxisLeft().setTextColor(resources.getColor(R.color.color_hint))
                    barchart.getAxisRight().setTextColor(resources.getColor(R.color.color_hint))
                    barchart.getXAxis().setTextColor(resources.getColor(R.color.color_hint))
                    barchart.getLegend().setTextColor(resources.getColor(R.color.color_hint))
                    barchart.setDrawBorders(false)
                    barchart.setVisibleXRangeMaximum(12f)
                    dataSets.add(barDataSet1)
                    val Data = BarData(dataSets);
                    barchart.setData(Data)
                    result_text.setText("Total Result - "+list.size)
                    val Adapter =
                       SalesOverTimeAdapter(this, list)
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
