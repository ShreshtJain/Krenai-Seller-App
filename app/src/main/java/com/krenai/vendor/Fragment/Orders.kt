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
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.datepicker.MaterialDatePicker
import com.krenai.vendor.Activity.Orders.Search_Orders
import com.krenai.vendor.Activity.calendar_activity
import com.krenai.vendor.Adapter.OrdersListingAdapter
import com.krenai.vendor.Adapter.ProductsListingAdapter
import com.krenai.vendor.R
import com.krenai.vendor.model.Dataorders
import com.krenai.vendor.model.Dataproductsimage
import com.krenai.vendor.model.ProductModel
import com.krenai.vendor.utils.jkeys.Keys.CommonResources.CONNECTION_URL_DEMAND
import kotlinx.android.synthetic.main.fragment_orders.*
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Orders :DialogFragment() {

    private lateinit var orderlisting_recycler: RecyclerView
    private lateinit var OrdersListingAdapter: OrdersListingAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var date:TextView
    private lateinit var totalsales:TextView
    private var dataordersList = arrayListOf<Dataorders>()
    private var orderedList=arrayListOf<Dataorders>()
    private var shippedList= arrayListOf<Dataorders>()
    private var inprocessList= arrayListOf<Dataorders>()
    lateinit var inprocess_button:LinearLayout
    lateinit var ordered_button:LinearLayout
    lateinit var shipped_button:LinearLayout

    var Fetch_orders = CONNECTION_URL_DEMAND+"api/v3/store/cart/supplier/"
    var shipped=0
    var shippedr=0.0
    var inprocess=0
    var inprocessr=0.0
    var ordered=0
    var orderedr=0.0
    var deliveredr=0.0
    private lateinit var ordered_count:TextView
    private lateinit var ordered_amount:TextView
    private lateinit var shipped_count:TextView
    private lateinit var shipped_amount:TextView
    private lateinit var inprocess_count:TextView
    private lateinit var inprocess_amount:TextView
    lateinit var dayformat:SimpleDateFormat
    lateinit var monthformat:SimpleDateFormat
    lateinit var yearformat:SimpleDateFormat
    lateinit var weekformat:SimpleDateFormat
    lateinit var daystring:String
    lateinit var monthstring:String
    lateinit var yearstring:String
    lateinit var daystring2:String
    lateinit var monthstring2:String
    lateinit var yearstring2:String
    lateinit var weekstring:String
    lateinit var weekstring2:String
    lateinit var dateFormat:SimpleDateFormat
    lateinit var shimmerFrameLayout:ShimmerFrameLayout
    lateinit var errortext:TextView
    lateinit var errorimage:ImageView
    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""
    var o=0
    var i=0
    var s=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_orders, container, false)
        inprocess_amount=v.findViewById(R.id.inprocess_amount)
        inprocess_count=v.findViewById(R.id.inprocess_count)
        shipped_amount=v.findViewById(R.id.shipped_amount)
        shipped_count=v.findViewById(R.id.shipped_count)
        ordered_amount=v.findViewById(R.id.ordered_amount)
        ordered_count=v.findViewById(R.id.ordered_count)
        totalsales=v.findViewById(R.id.totalsales)
        layoutManager = LinearLayoutManager(activity)
        errortext=v.findViewById(R.id.errortext)
        errorimage=v.findViewById(R.id.error_image);
        shipped_button=v.findViewById(R.id.shipped_button)
        ordered_button=v.findViewById(R.id.ordered_button)
        inprocess_button=v.findViewById(R.id.inprocess_button)

        OrdersListingAdapter = OrdersListingAdapter(activity as Context, dataordersList)

        date=v.findViewById(R.id.date)

        dateFormat = SimpleDateFormat("dd MMM yyyy ")
        dayformat=SimpleDateFormat("dd")
        monthformat=SimpleDateFormat("MMM")
        yearformat= SimpleDateFormat("yyyy")
        weekformat= SimpleDateFormat("EEE")
        //dateFormat_order=SimpleDateFormat("")
        val calendar = Calendar.getInstance()
        val date_s = dateFormat.format(calendar.getTime())
        daystring=dayformat.format(calendar.getTime())
        monthstring=monthformat.format(calendar.getTime())
        yearstring=yearformat.format(calendar.getTime())
        daystring2=dayformat.format(calendar.getTime())
        monthstring2=monthformat.format(calendar.getTime())
        yearstring2=yearformat.format(calendar.getTime())
        weekstring=weekformat.format(calendar.getTime())
        weekstring2=weekformat.format(calendar.getTime())
        date.text=date_s
        sharedPreferencesStatus = activity?.getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")
        Fetch_orders+=userid?.toDouble()?.toInt().toString()+"?sortBy=createdDate&sortOrder=desc&"
        shimmerFrameLayout = v.findViewById(R.id.shimmerLayout) as ShimmerFrameLayout

        val bottomSheet: View = v.findViewById(R.id.bottom_sheet)
        bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        BottomSheetBehavior.from(bottomSheet).peekHeight =
            (Resources.getSystem().getDisplayMetrics().heightPixels)/2
        date.setOnClickListener(){
            val builder = MaterialDatePicker.Builder.dateRangePicker()
            val now = Calendar.getInstance()
            builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
            val picker = builder.build()
            picker.show(activity?.supportFragmentManager!!, picker.toString())
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
                errortext.visibility= GONE
                errorimage.visibility= GONE
                setUpRecycler(v)
            }

        }

        setUpRecycler(v)
        orderlisting_recycler.adapter = OrdersListingAdapter
        orderlisting_recycler.layoutManager = layoutManager
        orderlisting_recycler.addItemDecoration(
            DividerItemDecoration

                (orderlisting_recycler.context, (layoutManager as LinearLayoutManager).orientation)
        )

        ordered_button.setOnClickListener {
            if(o==0){
                ordered_button.setBackgroundResource(R.color.yellow)
                if(orderedList.size==0)
                {
                    orderlisting_recycler.setVisibility(GONE)
                    errortext.setVisibility(VISIBLE)
                    errorimage.setVisibility(VISIBLE)
                }
                else{
                    errortext.setVisibility(GONE)
                    errorimage.setVisibility(GONE)
                    orderlisting_recycler.setVisibility(VISIBLE)
                }
                val OrderListingAdapter =
                    OrdersListingAdapter(activity as Context, orderedList)
                val mLayoutManager = LinearLayoutManager(activity)
                orderlisting_recycler.layoutManager = mLayoutManager
                orderlisting_recycler.adapter = OrderListingAdapter
                orderlisting_recycler.setHasFixedSize(true)
                o=1}
            else{
                ordered_button.setBackgroundResource(R.color.white)
                o=0
                if(dataordersList.size==0)
                {
                    orderlisting_recycler.setVisibility(GONE)
                    errortext.setVisibility(VISIBLE)
                    errorimage.setVisibility(VISIBLE)
                }
                else{
                    errortext.setVisibility(GONE)
                    errorimage.setVisibility(GONE)
                    orderlisting_recycler.setVisibility(VISIBLE)
                }
                val OrderListingAdapter =
                    OrdersListingAdapter(activity as Context, dataordersList)
                val mLayoutManager = LinearLayoutManager(activity)
                orderlisting_recycler.layoutManager = mLayoutManager
                orderlisting_recycler.adapter = OrderListingAdapter
                orderlisting_recycler.setHasFixedSize(true)
            }
            s=0;i=0
            inprocess_button.setBackgroundResource(R.color.white)
            shipped_button.setBackgroundResource(R.color.white)
        }
        shipped_button.setOnClickListener {
            if(s==0){
                if(shippedList.size==0)
                {
                    orderlisting_recycler.setVisibility(GONE)
                    errortext.setVisibility(VISIBLE)
                    errorimage.setVisibility(VISIBLE)
                }
                else{
                    errortext.setVisibility(GONE)
                    errorimage.setVisibility(GONE)
                    orderlisting_recycler.setVisibility(VISIBLE)
                }
                shipped_button.setBackgroundResource(R.color.yellow)
                s=1
                val OrderListingAdapter =
                    OrdersListingAdapter(activity as Context, shippedList)
                val mLayoutManager = LinearLayoutManager(activity)
                orderlisting_recycler.layoutManager = mLayoutManager
                orderlisting_recycler.adapter = OrderListingAdapter
                orderlisting_recycler.setHasFixedSize(true)
            }
            else{
                if(dataordersList.size==0)
                {
                    orderlisting_recycler.setVisibility(GONE)
                    errortext.setVisibility(VISIBLE)
                    errorimage.setVisibility(VISIBLE)
                }
                else{
                    errortext.setVisibility(GONE)
                    errorimage.setVisibility(GONE)
                    orderlisting_recycler.setVisibility(VISIBLE)
                }
                shipped_button.setBackgroundResource(R.color.white)
                s=0
                val OrderListingAdapter =
                    OrdersListingAdapter(activity as Context, dataordersList)
                val mLayoutManager = LinearLayoutManager(activity)
                orderlisting_recycler.layoutManager = mLayoutManager
                orderlisting_recycler.adapter = OrderListingAdapter
                orderlisting_recycler.setHasFixedSize(true)
            }

            inprocess_button.setBackgroundResource(R.color.white)
            ordered_button.setBackgroundResource(R.color.white)
            i=0
            o=0
        }
        inprocess_button.setOnClickListener {
            if(i==0){
                if(inprocessList.size==0)
                {
                    orderlisting_recycler.setVisibility(GONE)
                    errortext.setVisibility(VISIBLE)
                    errorimage.setVisibility(VISIBLE)
                }
                else{
                    errortext.setVisibility(GONE)
                    errorimage.setVisibility(GONE)
                    orderlisting_recycler.setVisibility(VISIBLE)
                }
                inprocess_button.setBackgroundResource(R.color.yellow)
                i=1
                val OrderListingAdapter =
                    OrdersListingAdapter(activity as Context, inprocessList)
                val mLayoutManager = LinearLayoutManager(activity)
                orderlisting_recycler.layoutManager = mLayoutManager
                orderlisting_recycler.adapter = OrderListingAdapter
                orderlisting_recycler.setHasFixedSize(true)
            }
            else{
                if(dataordersList.size==0)
                {
                    orderlisting_recycler.setVisibility(GONE)
                    errortext.setVisibility(VISIBLE)
                    errorimage.setVisibility(VISIBLE)
                }
                else{
                    errortext.setVisibility(GONE)
                    errorimage.setVisibility(GONE)
                    orderlisting_recycler.setVisibility(VISIBLE)
                }
                inprocess_button.setBackgroundResource(R.color.white)
                i=0
                val OrderListingAdapter =
                    OrdersListingAdapter(activity as Context, dataordersList,)
                val mLayoutManager = LinearLayoutManager(activity)
                orderlisting_recycler.layoutManager = mLayoutManager
                orderlisting_recycler.adapter = OrderListingAdapter
                orderlisting_recycler.setHasFixedSize(true)
            }
            shipped_button.setBackgroundResource(R.color.white)
            ordered_button.setBackgroundResource(R.color.white)
            s=0
            o=0
        }
        return v
    }

    private fun setUpRecycler(view: View) {
        shipped=0
        shippedr=0.0
        inprocess=0
        inprocessr=0.0
        ordered=0
        orderedr=0.0
        deliveredr=0.0
        dataordersList.clear()
        shippedList.clear()
        inprocessList.clear()
        orderedList.clear()
        orderlisting_recycler = view.findViewById(R.id.recycler_orders) as RecyclerView
        shimmerFrameLayout.setVisibility(VISIBLE)
        shimmerFrameLayout.startShimmer()
        orderlisting_recycler.setVisibility(GONE)

        val url=Fetch_orders+"startDate="+weekstring+"%20"+monthstring+"%20"+daystring+"%20"+yearstring+"%2000:00:00&endDate="+weekstring2+"%20"+monthstring2+"%20"+daystring2+"%20"+yearstring2+"%2023:59:59"
        Log.i("Order url",url)
        val queue = Volley.newRequestQueue(activity as Context)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url,
            Response.Listener<JSONObject>() { response ->
                orderlisting_recycler.setVisibility(VISIBLE)
                inprocess_button.setBackgroundResource(R.color.white)
                shipped_button.setBackgroundResource(R.color.white)
                ordered_button.setBackgroundResource(R.color.white)
                i=0;o=0;s=0;
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.setVisibility(View.GONE)
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    val data = response.getJSONArray("object")
                    for (i in 0 until data.length()) {
                        val OrderObject = data.getJSONObject(i)
                        val addresses=OrderObject.getJSONArray("addresses")
                        val state=OrderObject.getJSONObject("state")



                        for (j in 0 until addresses.length())
                        {
                            val OrdersModel = Dataorders(
                                addresses.getJSONObject(j).getString("customerName"),
                                String.format("%.2f",(OrderObject.getString("orderAmount")).toDouble()),
                                addresses.getJSONObject(j).getString("contactNo"),
                                "#OD - "+OrderObject.getString("id"),
                                state.getString("description"),
                                OrderObject.getString("paymentMode"),
                                OrderObject.getString("createdDate"),
                                OrderObject.getString("updatedDate"),
                                OrderObject.getString("updatedBy"),
                                i,OrderObject
                            )
                            dataordersList.add(OrdersModel)
                        }
                    }
                    if(dataordersList.size<1)
                    {
                        errortext.setVisibility(VISIBLE)
                        orderlisting_recycler.setVisibility(GONE)
                    }
                    setData(dataordersList)
                    if (activity != null) {
                        val OrderListingAdapter =
                            OrdersListingAdapter(activity as Context, dataordersList)
                        val mLayoutManager = LinearLayoutManager(activity)
                        orderlisting_recycler.layoutManager = mLayoutManager
                        orderlisting_recycler.adapter = OrderListingAdapter
                        orderlisting_recycler.setHasFixedSize(true)
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
                headers["Authorization"] = " Bearer "+token

                return headers
            }
        }
        queue.add(jsonObjectRequest)

    }
    private fun setData(arrayList: ArrayList<Dataorders>)
    {
        if(dataordersList.size==0)
        {
            orderlisting_recycler.setVisibility(GONE)
            errortext.setVisibility(VISIBLE)

        }
        else
        {
            errortext.setVisibility(GONE)
            orderlisting_recycler.setVisibility(VISIBLE)
        }
        for (i in 0 until dataordersList.size)
        {
            if((dataordersList[i].description).toUpperCase()=="SHIPPED")
            {
                shippedList.add(dataordersList[i])
                shipped++
                shippedr=shippedr + (dataordersList[i].Cost).toDouble()
            }
            else if((dataordersList[i].description).toUpperCase()=="ORDERED")
            {
                orderedList.add(dataordersList[i])
                orderedr=orderedr + (dataordersList[i].Cost).toDouble()
                ordered++
            }
            else if((dataordersList[i].description).toUpperCase()=="INPROCESS")
            {
                inprocessList.add(dataordersList[i])
                inprocessr=inprocessr + (dataordersList[i].Cost).toDouble()
                inprocess++
            }
            else{
                deliveredr+=(dataordersList[i].Cost).toDouble()
            }

        }
        totalsales.setText(String.format("%.2f",(inprocessr+shippedr+orderedr+deliveredr)))
        inprocess_count.setText(inprocess.toString())
        inprocess_amount.setText(String.format("%.2f",inprocessr))
        ordered_count.setText(ordered.toString())
        ordered_amount.setText(String.format("%.2f",orderedr))
        shipped_count.setText(shipped.toString())
        shipped_amount.setText(String.format("%.2f",shippedr))


    }

}
