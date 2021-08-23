package com.krenai.vendor.Adapter

import SliderItemDecoration
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PointF.length
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

import com.krenai.vendor.R
import com.krenai.vendor.model.DataCategory
import com.krenai.vendor.model.DataOrderDetails
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.products_custom_row.view.*
import org.json.JSONException
import org.json.JSONObject
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderDetailsAdapter(val context: Context, val itemList: ArrayList<DataOrderDetails>) :
    RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderDetailsAdapter.OrderDetailsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_details_row, parent, false)

        return OrderDetailsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: OrderDetailsAdapter.OrderDetailsViewHolder,
        position: Int
    ) {
        val data = itemList[position]

        holder.name.text = data.name
        holder.description.text = data.description

        if(data.mediaUrl.length>0)
             Picasso.get().load(data.mediaUrl).error(R.drawable.broken_image).into(holder.image)
        else
            Picasso.get().load(R.drawable.broken_image).into(holder.image)

        holder.quantity.text=data.quantity.toString()
        holder.amount.text="₹ "+data.amount.toString()

        holder.add.setOnClickListener {
            holder.quantity.setText((data.quantity+1).toString())
            data.quantity=holder.quantity.text.toString().toInt()
        }
        holder.minus.setOnClickListener {
            if(data.quantity>0) {
                holder.quantity.setText((data.quantity - 1).toString())
                data.quantity=holder.quantity.text.toString().toInt()
            }
        }
    }

    class OrderDetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.Product_name) as TextView
        val description = view.findViewById<TextView>(R.id.description)
        val image = view.findViewById(R.id.product_image) as ImageView
        val quantity=view.findViewById<TextView>(R.id.quantity)
        val amount=view.findViewById<TextView>(R.id.amount)
        val minus=view.findViewById<TextView>(R.id.minus)
        val add=view.findViewById<TextView>(R.id.plus)
    }
}