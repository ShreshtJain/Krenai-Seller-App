package com.krenai.vendor.Adapter


import SliderItemDecoration
import android.content.Context
import android.content.Intent
import android.graphics.PointF.length
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper

import androidx.recyclerview.widget.RecyclerView
import com.krenai.vendor.Activity.Orders.Order_Details
import com.krenai.vendor.Activity.Products.Create_Product

import com.krenai.vendor.R
import com.krenai.vendor.model.Dataorders
import kotlinx.android.synthetic.main.products_custom_row.view.*
import org.json.JSONArray
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrdersListingAdapter(val context: Context, val itemList: ArrayList<Dataorders>) :
    RecyclerView.Adapter<OrdersListingAdapter.OrdersListingViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()
    private lateinit var OrdersListingImageRecycler: RecyclerView


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrdersListingAdapter.OrdersListingViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.orders_custom_row, parent, false)
        return OrdersListingViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: OrdersListingAdapter.OrdersListingViewHolder,
        position: Int
    ) {
        val dataorders = itemList[position]

       holder.itemView.setOnClickListener{
           val intent=Intent(context, Order_Details::class.java)
           intent.putExtra("name",dataorders.CustomerName)
           intent.putExtra("amount",dataorders.Cost)
           intent.putExtra("contact",dataorders.Contact)
           intent.putExtra("id",dataorders.Orderid)
           intent.putExtra("description",dataorders.description)
           intent.putExtra("updatedBy",dataorders.updatedBy)
           intent.putExtra("updatedDate",dataorders.updatedDate)
           intent.putExtra("position",dataorders.position)
           intent.putExtra("object",dataorders.jsonObject.toString())
           context.startActivity(intent)
       }
        val date= dataorders.createdDate.subSequence(0,10).toString()
        var time=dataorders.createdDate.subSequence(11,19).toString()
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
        holder.date_order.text=time+" | " +date
        holder.name.text =dataorders.CustomerName
        holder.amount.text = dataorders.Cost
        holder.contact.text = dataorders.Contact
        holder.id.text=dataorders.Orderid
        holder.description.text=dataorders.description
        holder.paymentmode.text=dataorders.paymentmode

    }

    class OrdersListingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById(R.id.name) as TextView
        val amount = view.findViewById(R.id.amount) as TextView
        val contact: TextView = view.findViewById(R.id.contact) as TextView
        val id: TextView = view.findViewById(R.id.orderid) as TextView
        val description=view.findViewById<TextView>(R.id.description)
        val paymentmode=view.findViewById<TextView>(R.id.paymentmode)
        val date_order=view.findViewById<TextView>(R.id.date_order)
    }
}