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
import com.krenai.vendor.model.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.category_filter_row.view.*
import kotlinx.android.synthetic.main.products_custom_row.view.*
import org.json.JSONException
import org.json.JSONObject
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SalesByProductAdapter(val context: Context?, val itemList: ArrayList<DataSalesByProduct>) :
    RecyclerView.Adapter<SalesByProductAdapter.SalesByProductViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()
    private var sharedPreferencesStatus: SharedPreferences? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SalesByProductAdapter.SalesByProductViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.sales_by_product_custom_row, parent, false)
        sharedPreferencesStatus = context?.getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        return SalesByProductViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: SalesByProductAdapter.SalesByProductViewHolder,
        position: Int
    ) {
        val data = itemList[position]
        holder.date.text = data.date
        Picasso.get().load(data.url).error(R.drawable.broken_image).into(holder.image)
        holder.net_quantity.text=data.productName
        holder.category_name.text=data.categoryName
        holder.product_name.text=data.productName
        holder.sub_category_name.text=data.subcategoryName
        holder.sub_sub_category_name.text=data.subsubcategoryName
        holder.net_quantity.text=data.quantity
        holder.sku.text=data.sku
        holder.sp.text="₹ "+String.format("%.2f",(data.sp).toDouble())
        holder.net_sale.text="₹ "+String.format("%.2f",(data.net_sale).toDouble())
        holder.total_sale.text="₹ "+String.format("%.2f",(data.total_sale).toDouble())
    }

    class SalesByProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date= view.findViewById(R.id.date) as TextView
        val image= view.findViewById(R.id.image) as ImageView
        val product_name=view.findViewById<TextView>(R.id.product_name)
        val category_name= view.findViewById(R.id.category_name) as TextView
        val sub_category_name= view.findViewById(R.id.sub_category_name) as TextView
        val sub_sub_category_name= view.findViewById(R.id.sub_sub_category_name) as TextView
        val net_quantity= view.findViewById(R.id.net_quantity) as TextView
        val sku= view.findViewById(R.id.sku_code) as TextView
        val sp= view.findViewById(R.id.sp) as TextView
        val net_sale= view.findViewById(R.id.net_sale) as TextView
        val total_sale= view.findViewById(R.id.total_sale) as TextView
    }
}