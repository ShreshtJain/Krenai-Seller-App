package com.krenai.vendor.Adapter

import SliderItemDecoration
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PointF.length
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper

import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.krenai.vendor.Activity.Orders.Order_Details
import com.krenai.vendor.Activity.Products.Create_Product
import com.krenai.vendor.Activity.Products.VariantDetails

import com.krenai.vendor.R
import com.krenai.vendor.model.DataBrand
import com.krenai.vendor.model.DataCategory
import com.krenai.vendor.model.DataOrderDetails
import com.krenai.vendor.model.DataSalesOverTime
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.category_filter_row.view.*
import kotlinx.android.synthetic.main.products_custom_row.view.*
import org.json.JSONException
import org.json.JSONObject
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SalesOverTimeAdapter(val context: Context?, val itemList: ArrayList<DataSalesOverTime>) :
    RecyclerView.Adapter<SalesOverTimeAdapter.SalesOverTimeViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()
    private var sharedPreferencesStatus: SharedPreferences? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SalesOverTimeAdapter.SalesOverTimeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.sales_over_time_custom_row, parent, false)
        sharedPreferencesStatus = context?.getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        return SalesOverTimeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: SalesOverTimeAdapter.SalesOverTimeViewHolder,
        position: Int
    ) {
        var data = itemList[position]
        holder.date.text = data.date
        holder.Return.text="₹ "+data.Return
        holder.order.text=data.order
        holder.net_sales.text="₹ "+String.format("%.2f",(data.net_sales).toDouble())
        holder.discount.text="₹ "+String.format("%.2f",(data.discount).toDouble())
        holder.gross_sales.text="₹ "+String.format("%.2f",(data.gross_sales).toDouble())
        holder.shipping.text="₹ "+String.format("%.2f",(data.shipping).toDouble())
        holder.tax.text="₹ "+String.format("%.2f",(data.tax).toDouble())
        holder.total_sales.text="₹ "+String.format("%.2f",(data.total_sales).toDouble())
    }

    class SalesOverTimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date= view.findViewById(R.id.date) as TextView
        val order= view.findViewById(R.id.order) as TextView
        val gross_sales= view.findViewById(R.id.gross_sales) as TextView
        val discount= view.findViewById(R.id.discount) as TextView
        val Return= view.findViewById(R.id.Return) as TextView
        val net_sales= view.findViewById(R.id.net_sales) as TextView
        val shipping= view.findViewById(R.id.shipping) as TextView
        val tax= view.findViewById(R.id.tax) as TextView
        val total_sales= view.findViewById(R.id.total_sales) as TextView
    }
}