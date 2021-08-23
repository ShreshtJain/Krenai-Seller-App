package com.krenai.vendor.Fragment


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.datepicker.MaterialDatePicker
import com.krenai.vendor.Activity.Home.*
import com.krenai.vendor.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class Dashboard : Fragment() {
    private lateinit var date: TextView

    lateinit var finances_summary: TextView
    lateinit var total_sales_finances: TextView
    lateinit var customer_over_time: TextView
    lateinit var sales_by_product: TextView
    lateinit var sales_over_time: TextView
    lateinit var avg_order: TextView
    lateinit var reviews:TextView

    lateinit var dateFormat: SimpleDateFormat
    lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>

    lateinit var barchart: BarChart
    lateinit var inner: LinearLayout

    lateinit var revenue: TextView
    lateinit var totalcustomers: TextView
    lateinit var totalsales: TextView
    lateinit var totalproducts: TextView
    lateinit var totalorders: TextView
    lateinit var orders: TextView
    lateinit var products: TextView
    lateinit var customers: TextView
    lateinit var sales: TextView

    lateinit var name: TextView

    private lateinit var date_s: String
    lateinit var dayformat: SimpleDateFormat
    lateinit var monthformat: SimpleDateFormat
    lateinit var yearformat: SimpleDateFormat
    lateinit var weekformat: SimpleDateFormat
    lateinit var daystring: String
    lateinit var monthstring: String
    lateinit var yearstring: String
    lateinit var daystring2: String
    lateinit var monthstring2: String
    lateinit var yearstring2: String
    lateinit var weekstring: String
    lateinit var weekstring2: String

    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid: String? = ""
    private var token: String? = ""

    private var refresh_token: String? = ""

    var url1 = "https://www.outsourcecto.com/api/v3/reports/count/"
    var barcharturl = "https://www.outsourcecto.com/api/v3/reports/orders/from/char-data/"
    var url = "https://www.krenai.online/api/v3/account/suppliers/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        date = view.findViewById(R.id.date)
        dateFormat = SimpleDateFormat("dd MMM yyyy ")
        dayformat = SimpleDateFormat("dd")
        monthformat = SimpleDateFormat("MMM")
        yearformat = SimpleDateFormat("yyyy")
        weekformat = SimpleDateFormat("EEE")
        val calendar = Calendar.getInstance()
        daystring = dayformat.format(calendar.getTime())
        monthstring = monthformat.format(calendar.getTime())
        yearstring = yearformat.format(calendar.getTime())
        daystring2 = dayformat.format(calendar.getTime())
        monthstring2 = monthformat.format(calendar.getTime())
        yearstring2 = yearformat.format(calendar.getTime())
        weekstring = weekformat.format(calendar.getTime())
        weekstring2 = weekformat.format(calendar.getTime())
        date_s = dateFormat.format(calendar.getTime())
        date.text = date_s

        sharedPreferencesStatus = activity?.getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid = sharedPreferencesStatus?.getString("USER_ID", "")
        token = sharedPreferencesStatus?.getString("TOKEN", "")
        refresh_token = sharedPreferencesStatus?.getString("Refresh_Token", "")

        url1 = url1 + userid?.toDouble()?.toInt().toString()
        barcharturl = barcharturl + userid?.toDouble()?.toInt().toString() + "?endDate="
        url += userid?.toDouble()?.toInt().toString()
        /*chart*/
        barchart = view.findViewById(R.id.barchart) as BarChart
        dataBarChart()

        revenue = view.findViewById<TextView>(R.id.revenue)
        totalcustomers = view.findViewById<TextView>(R.id.totalcustomers)
        totalproducts = view.findViewById<TextView>(R.id.totalproducts)
        totalorders = view.findViewById<TextView>(R.id.totalorders)
        totalsales = view.findViewById<TextView>(R.id.totalsales)
        orders = view.findViewById<TextView>(R.id.orders)
        products = view.findViewById<TextView>(R.id.products)
        customers = view.findViewById<TextView>(R.id.customers)
        sales = view.findViewById<TextView>(R.id.sales)

        sales_over_time = view.findViewById(R.id.sales_over_time)
        sales_by_product = view.findViewById(R.id.sales_by_product)
        finances_summary = view.findViewById(R.id.finances_summary)
        customer_over_time = view.findViewById(R.id.customer_over_time)
        total_sales_finances = view.findViewById(R.id.total_sales_finances)
        avg_order = view.findViewById(R.id.avg_order)
        reviews=view.findViewById(R.id.reviews)

        name = view.findViewById(R.id.name) as TextView

        total_sales_finances.setOnClickListener {
            startActivity(Intent(activity, FinanceSales::class.java))
        }
        finances_summary.setOnClickListener {
            startActivity(Intent(activity, FinanceSummary::class.java))
        }
        sales_by_product.setOnClickListener {
            startActivity(Intent(activity, SalesByProduct::class.java))
        }
        avg_order.setOnClickListener {
            startActivity(Intent(activity, AvgOrder::class.java))
        }
        customer_over_time.setOnClickListener {
            startActivity(Intent(activity,CustomersOverTime::class.java))
        }
        reviews.setOnClickListener {
            startActivity(Intent(activity,CustomerReviews::class.java))
        }

        date.setOnClickListener {
            val builder = MaterialDatePicker.Builder.dateRangePicker()
            val now = Calendar.getInstance()
            builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
            val picker = builder.build()
            picker.show(activity?.supportFragmentManager!!, picker.toString())
            picker.addOnPositiveButtonClickListener {
                val first = Date((it.first) as Long)
                val second = Date((it.second) as Long)
                val f = dateFormat.format(first)
                val s = dateFormat.format(second)
                daystring = dayformat.format(first)
                weekstring = weekformat.format(first)
                monthstring = monthformat.format(first)
                yearstring = yearformat.format(first)
                daystring2 = dayformat.format(second)
                monthstring2 = monthformat.format(second)
                yearstring2 = yearformat.format(second)
                weekstring2 = weekformat.format(second)
                date.setText(f + " - " + s)
                getdynamicdata()
                dataBarChart()
            }
        }

        val bottomSheet: View = view.findViewById(R.id.bottom_sheet)
        bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        inner = view.findViewById(R.id.inner)

        BottomSheetBehavior.from(bottomSheet).peekHeight =
            ((Resources.getSystem().getDisplayMetrics().heightPixels) / 2.2).toInt()
        sales_over_time.setOnClickListener {
            startActivity(Intent(activity, SalesOverTime::class.java))
        }

        setName()
        getdata()
        getdynamicdata()
        return view
    }


    private fun dataBarChart() {
        val queue = Volley.newRequestQueue(activity as Context)
        val url =
            barcharturl + weekstring2 + "%20" + monthstring2 + "%20" + daystring2 + "%20" + yearstring2
        Log.i("databarchart", url)
        val jsonObjectRequest = object : JsonArrayRequest(
            Request.Method.GET, url,
            Response.Listener<JSONArray>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    val data = response.getJSONObject(0)
                    val android = data.getJSONArray("android")
                    val ios = data.getJSONArray("ios")
                    val panel = data.getJSONArray("panel")
                    val pos = data.getJSONArray("pos")
                    val online = arrayListOf<Float>()
                    val offline = arrayListOf<Float>()
                    val Jsonlabels = response.getJSONArray(1)
                    val labels = arrayListOf<String>()
                    for (i in 0 until Jsonlabels.length()) {
                        labels.add(Jsonlabels.getString(i))
                    }
                    barchart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

                    for (i in 0 until android.length()) {
                        online.add(i, android.getInt(i).toFloat() + ios.getInt(i).toFloat())
                    }
                    for (i in 0 until panel.length()) {
                        offline.add(i, panel.getInt(i).toFloat() + pos.getInt(i).toFloat())
                    }

                    val entries1 = arrayListOf<BarEntry>()
                    for (i in 0 until online.size) {
                        entries1.add(BarEntry((i).toFloat(), online[i]))
                    }
                    val entries2 = arrayListOf<BarEntry>()
                    for (i in 0 until offline.size) {
                        entries2.add(BarEntry((i).toFloat(), offline[i]))
                    }

                    if (activity != null) {
                        barchart.setTouchEnabled(true);
                        barchart.setDragEnabled(true);
                        barchart.setScaleEnabled(true);
                        barchart.animateXY(3000, 3000);
                        barchart.setHorizontalScrollBarEnabled(true);
                        barchart.setDoubleTapToZoomEnabled(true)

                        val dataSets = arrayListOf<IBarDataSet>()

                        val barDataSet1 = BarDataSet(entries1, "ONLINE ORDERS")
                        barDataSet1.setColors(
                            resources.getColor(R.color.yellow)
                        )
                        val barDataSet2 = BarDataSet(entries2, "OFFLINE ORDERS")
                        barDataSet2.setColors(
                            resources.getColor(R.color.blue)
                        )
                        barDataSet1.setValueTextColor(Color.WHITE)
                        barDataSet2.setValueTextColor(Color.BLACK)
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
                        dataSets.add(barDataSet2)
                        val Data = BarData(dataSets);
                        barchart.setData(Data);

                    }
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
                headers["Authorization"] = " Bearer " + token

                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun getdata() {
        Log.i("getdata", url1)
        val queue = Volley.newRequestQueue(activity as Context)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url1,
            Response.Listener<JSONObject>() { response ->
                try {
                    totalcustomers.setText(response.getString("totalCustomerCount"))
                    totalproducts.setText(response.getString("totalProducts"))
                    totalorders.setText(response.getString("totalOrder"))

                    val s = String.format("%.0f", (response.getString("totalRevenue")).toDouble())
                    totalsales.setText(s)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError? ->
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers =
                    HashMap<String, String>()
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                headers["Authorization"] = " Bearer " + token

                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun getdynamicdata() {
        val queue = Volley.newRequestQueue(activity as Context)
        val startdate =
            "?startDate=" + weekstring + "%20" + monthstring + "%20" + daystring + "%20" + yearstring
        val enddate =
            "%2000:00:00&endDate=" + weekstring2 + "%20" + monthstring2 + "%20" + daystring2 + "%20" + yearstring2 + "%2023:59:59"
        val urld = url1 + startdate + enddate
        Log.i("dynamicdata", urld)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, urld,
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    val s = String.format("%.2f", (response.getString("totalRevenue")).toDouble())
                    revenue.setText(s)
                    sales.setText(String.format("%.0f", (response.getString("totalRevenue")).toDouble()))
                    customers.setText(response.getString("totalCustomerCount"))
                    products.setText(response.getString("inStockProducts"))
                    orders.setText(response.getString("totalOrder"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError? ->
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers =
                    HashMap<String, String>()
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                headers["Authorization"] = " Bearer " + token

                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun setName() {
        val queue = Volley.newRequestQueue(activity as Context)
        Log.i("setName", url)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url,
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    val data = response.getJSONArray("object")

                    for (i in 0 until data.length()) {
                        val profileobject = data.getJSONObject(i)

                        val f: String =
                            profileobject.getString("firstName") + " " + profileobject.getString("lastName")
                        name.setText(f)
                    }
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
                headers["Authorization"] = " Bearer " + token

                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }
}







