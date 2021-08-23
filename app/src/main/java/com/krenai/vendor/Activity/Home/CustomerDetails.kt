package com.krenai.vendor.Activity.Home

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.krenai.vendor.Adapter.CustomersTabAdapter
import com.krenai.vendor.Adapter.ProductsListingAdapter
import com.krenai.vendor.HeightWrappingViewPager
import com.krenai.vendor.R
import com.krenai.vendor.model.DataVariantMatrix
import com.krenai.vendor.model.Dataproductsimage
import com.krenai.vendor.model.ProductModel
import com.krenai.vendor.utils.jkeys.Keys.CommonResources.CONNECTION_URL
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_variant_details.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class CustomerDetails : AppCompatActivity() {
    lateinit var barchart: BarChart
    lateinit var dayformat: SimpleDateFormat
    lateinit var monthformat: SimpleDateFormat
    lateinit var yearformat: SimpleDateFormat
    lateinit var weekformat: SimpleDateFormat

    lateinit var amount:TextView
    lateinit var description:TextView
    lateinit var date:TextView
    lateinit var emailId:TextView
    lateinit var contact:TextView
    lateinit var name:TextView
    lateinit var textImage:MaterialButton
    lateinit var profileImage: ImageView
    lateinit var parentLinearLayout:LinearLayout
    var tabLayoutTime:TabLayout?=null
    var viewPager: HeightWrappingViewPager?=null
    lateinit var tabLayout:TabLayout
    lateinit var clear:Button

    lateinit var creditBook:TextView
    lateinit var totalOrders:TextView
    lateinit var walletBalance:TextView
    lateinit var promoBalance:TextView
    var jsonObject= JSONObject()
    var cart=JSONArray()
    var promoWalletUrl=""
    var creditBookUrl=""
    var mainWalletUrl=""
    var customerId:String=""

    val entries1 = arrayListOf<BarEntry>()
    val labels= arrayListOf<String>()

    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid: String? = ""
    private var token: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_details)

        sharedPreferencesStatus =getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")

        jsonObject= JSONObject(getIntent().getStringExtra("object") as String)
        customerId=jsonObject.getString("id")

        promoWalletUrl=CONNECTION_URL+"api/v3/account/wallet/promo/customer/"+customerId
        mainWalletUrl=CONNECTION_URL+"api/v3/account/wallet/customer/"+customerId
        creditBookUrl=CONNECTION_URL+"api/v3/account/credit-book/customer/"+customerId

        barchart = findViewById(R.id.barchart) as BarChart
        dataBarChart()

        tabLayoutTime=findViewById(R.id.tabLayoutTime)
        dayformat = SimpleDateFormat("dd")
        monthformat = SimpleDateFormat("MMM")
        yearformat = SimpleDateFormat("yyyy")
        weekformat = SimpleDateFormat("EEE")
        onAddTimeTab()

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)


        clear=findViewById(R.id.clear)

        clear.setOnClickListener {
            finish()
        }
        parentLinearLayout=findViewById(R.id.parentLinear)
        amount=findViewById(R.id.amount)
        description=findViewById(R.id.description)
        date=findViewById(R.id.date)
        emailId=findViewById(R.id.emailId)
        contact=findViewById(R.id.contact)
        name=findViewById(R.id.name)
        textImage=findViewById(R.id.text_image)
        profileImage=findViewById(R.id.profile_image_user)


        creditBook=findViewById(R.id.credit_book)
        totalOrders=findViewById(R.id.total_orders)
        walletBalance=findViewById(R.id.wallet_balance)
        promoBalance=findViewById(R.id.promo_balance)


        name.setText(getIntent().getStringExtra("name"))
        description.setText(getIntent().getStringExtra("description"))
        emailId.setText(getIntent().getStringExtra("emailId"))
        contact.setText(getIntent().getStringExtra("contact"))
        date.setText(getIntent().getStringExtra("date"))
        textImage.setText(name.text[0].toString())

        if(jsonObject.getString("profilePicUrl").toUpperCase().equals("NULL") || jsonObject.getString("profilePicUrl").equals(""))
        {
            textImage.visibility=View.VISIBLE
            profileImage.visibility=View.GONE
        }
        else
        {
            textImage.visibility=View.GONE
            profileImage.visibility=View.VISIBLE
            Picasso.get().load(jsonObject.getString("profilePicUrl")).error(R.drawable.broken_image)
                .into(profileImage)
        }

        tabLayout.addTab(tabLayout.newTab().setText("Addresses"))
        tabLayout.addTab(tabLayout.newTab().setText("Orders History"))
        tabLayout.addTab(tabLayout.newTab().setText("Wallet Transactions"))
        tabLayout.addTab(tabLayout.newTab().setText("Promo Transactions"))
        tabLayout.addTab(tabLayout.newTab().setText("Credit Book Transactions"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        onAddTab()
    }
    private fun onAddTab()
    {
        val adapter = CustomersTabAdapter(this, supportFragmentManager, tabLayout.tabCount,jsonObject,CONNECTION_URL+"api/v3/customers/customer/"+customerId,promoWalletUrl,
        mainWalletUrl,creditBookUrl)
        viewPager?.adapter = adapter
        viewPager?.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        viewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager?.currentItem = tab.position

            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

    }
    private fun onAddTimeTab()
    {
        tabLayoutTime!!.addTab(tabLayoutTime!!.newTab().setText("Lifetime"))
        tabLayoutTime!!.addTab(tabLayoutTime!!.newTab().setText("7 days"))
        tabLayoutTime!!.addTab(tabLayoutTime!!.newTab().setText("1 day"))
        tabLayoutTime!!.tabGravity = TabLayout.GRAVITY_FILL

        tabLayoutTime!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if(tab.position==0)
                {
                    val s="lifetime"
                    dataBarChartTab(s)


                }
                else if(tab.position==1)
                {
                    val calendar2=Calendar.getInstance()
                    calendar2.add(Calendar.DAY_OF_WEEK, -7)

                    val daystring=dayformat.format(calendar2.getTime())
                    val monthstring=monthformat.format(calendar2.getTime())

                    val s=monthstring+" "+daystring
                    dataBarChartTab(s)

                }
                else if(tab.position==2)
                {
                    val calendar2=Calendar.getInstance()
                    calendar2.add(Calendar.DAY_OF_WEEK, -1)

                    val daystring=dayformat.format(calendar2.getTime())
                    val monthstring=monthformat.format(calendar2.getTime())

                    val s=monthstring+" "+daystring
                    dataBarChartTab(s)
                }

            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    fun dataBarChart()
    {
        val arr= arrayOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
        val datelist= arrayListOf<String>()
        val bar_list= arrayListOf<Float>()
        val queue = Volley.newRequestQueue(this)
        val url=CONNECTION_URL+"api/v3/customers/customer/"+customerId
        Log.i("cart url",url)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url,
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    val data = response.getJSONArray("object")
                    val Object=data.getJSONObject(0)
                    try{
                        val wallet=Object.getJSONObject("wallet")
                        if(wallet.getString("balancePont").toLowerCase()=="null") {
                            walletBalance.setText("₹ " + "00")
                        }
                        else{
                            walletBalance.setText("₹ " + wallet.getString("balancePont"))
                        }

                    }
                    catch (e:JSONException)
                    {
                        walletBalance.setText("₹ " + "00")
                    }
                    try{
                        val wallet=Object.getJSONObject("promoWallet")
                        if(wallet.getString("balancePont").toLowerCase()=="null") {
                            promoBalance.setText("₹ " + "00")
                        }
                        else{
                           promoBalance.setText("₹ " + wallet.getString("balancePont"))
                        }
                    }
                    catch(e:JSONException)
                    {
                        promoBalance.setText("₹ "+"00")
                    }

                    try{
                        val wallet=Object.getJSONObject("creditBook")
                        if(wallet.getString("balancePont").toLowerCase()=="null") {
                            creditBook.setText("₹ "+"00")
                        }
                        else{
                            creditBook.setText("₹ " + wallet.getString("balancePoint"))
                        }
                    }
                    catch(e:JSONException)
                    {
                        creditBook.setText("₹ "+"00")
                    }

                    totalOrders.setText(Object.getString("totalOrderCount"))
                    amount.setText("₹ "+Object.getString("totalOrderAmount"))

                    if(Object.getJSONArray("cart").length()>0) {
                        cart = Object.getJSONArray("cart")

                        Log.i("cart length",cart.length().toString())

                        for(i in 0 until cart.length())
                        {
                            bar_list.add(cart.getJSONObject(i).getString("orderAmount").toFloat())
                            datelist.add(cart.getJSONObject(i).getString("createdDate"))
                        }

                        for (i in 0 until datelist.size)
                        {
                            val s1=datelist[i].subSequence(5,7).toString().toInt()
                            val s2=datelist[i].subSequence(8,10).toString()
                            val s3=arr[s1-1]
                            labels.add(s3+" "+s2)
                        }
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

                        val barDataSet1 = BarDataSet(entries1, "ORDER AMOUNT")
                        barDataSet1.setColors(
                            resources.getColor(R.color.yellow)
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
                        //barchart.setVisibleXRangeMaximum(12f)
                        dataSets.add(barDataSet1)
                        val Data = BarData(dataSets);
                        barchart.setData(Data)
                    }
                    Log.i("cart",cart.length().toString())

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
                headers["Authorization"] = " Bearer "+token

                return headers
            }
        }
        queue.add(jsonObjectRequest)

    }
    fun  dataBarChartTab(s:String)
    {
        if(s.equals("lifetime"))
        {
            barchart.setTouchEnabled(true);
            barchart.setDragEnabled(true);
            barchart.setScaleEnabled(true);
            barchart.animateXY(3000, 3000);
            barchart.setHorizontalScrollBarEnabled(true);
            barchart.setDoubleTapToZoomEnabled(true)
            val dataSets = arrayListOf<IBarDataSet>()

            val barDataSet1 = BarDataSet(entries1, "ORDER AMOUNT")
            barDataSet1.setColors(
                resources.getColor(R.color.yellow)
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
            //barchart.setVisibleXRangeMaximum(12f)
            dataSets.add(barDataSet1)
            val Data = BarData(dataSets);
            barchart.setData(Data)
        }
        else
        {
            Log.i("iiii",s.subSequence(4,6).toString())
            Log.i("iiii",s.subSequence(0,3).toString())
            val entriesTemp = arrayListOf<BarEntry>()
            val labelsTemp= arrayListOf<String>()
            for(i in 0 until labels.size)
            {
                if(s.subSequence(0,3).toString().equals(labels[i].subSequence(0,3).toString()))
                {
                    if(s.subSequence(4,6).toString().toInt()<=(labels[i].subSequence(4,6).toString().toInt()))
                    {
                        Log.i("iiii",s)
                        for(j in i until entries1.size)
                        {
                            entriesTemp.add(entries1[j])
                        }
                        barchart.setTouchEnabled(true);
                        barchart.setDragEnabled(true);
                        barchart.setScaleEnabled(true);
                        barchart.animateXY(3000, 3000);
                        barchart.setHorizontalScrollBarEnabled(true);
                        barchart.setDoubleTapToZoomEnabled(true)
                        val dataSets = arrayListOf<IBarDataSet>()

                        val barDataSet1 = BarDataSet(entriesTemp, "ORDER AMOUNT")
                        barDataSet1.setColors(
                            resources.getColor(R.color.yellow)
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
                        //barchart.setVisibleXRangeMaximum(12f)
                        dataSets.add(barDataSet1)
                        val Data = BarData(dataSets);
                        barchart.setData(Data)

                        return
                    }

                }
            }
            barchart.setTouchEnabled(true);
            barchart.setDragEnabled(true);
            barchart.setScaleEnabled(true);
            barchart.animateXY(3000, 3000);
            barchart.setHorizontalScrollBarEnabled(true);
            barchart.setDoubleTapToZoomEnabled(true)
            val dataSets = arrayListOf<IBarDataSet>()

            val barDataSet1 = BarDataSet(entriesTemp, "ORDER AMOUNT")
            barDataSet1.setColors(
                resources.getColor(R.color.yellow)
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
            //barchart.setVisibleXRangeMaximum(12f)
            dataSets.add(barDataSet1)
            val Data = BarData(dataSets);
            barchart.setData(Data)

            barchart.setNoDataText("No orders yet")

            return
        }
    }
}
