package com.krenai.vendor.Adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.krenai.vendor.Activity.Home.CustomerDetails
import com.krenai.vendor.Activity.Orders.Order_Details

import com.krenai.vendor.R
import com.krenai.vendor.model.DataCreditModel
import com.krenai.vendor.model.Dataorders
import org.json.JSONObject
import kotlin.collections.ArrayList

class OrderHistoryAdapter(val context: Context, val itemList: ArrayList<DataCreditModel>) :
    RecyclerView.Adapter<OrderHistoryAdapter.CustomerOverTimeViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):CustomerOverTimeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.customer_order_custom_row, parent, false)

        return CustomerOverTimeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: CustomerOverTimeViewHolder,
        position: Int
    ) {
        val Object = itemList[position]

        holder.id.setText("#OD - "+Object.transactionId)

        holder.status.setText(Object.type)

        if (Object.type.toUpperCase().equals("DELIVERED")) {
            holder.status.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.green)))
        }
        else if(Object.type.toUpperCase().equals("ORDERED"))
        {
            holder.status.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.red)))
        }
        else if(Object.type.toUpperCase().equals("INPROCESS"))
        {
            holder.status.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.blue)))
        }

        val createdDate = Object.date

        val date = createdDate.subSequence(0, 10).toString()
        var time = createdDate.subSequence(11, 19).toString()

        if (time.subSequence(0, 2).toString().toInt() > 12) {
            var t = (time.subSequence(0, 2).toString().toInt() - 12).toString()
            if (t.length < 2) {
                t = "0" + t
            }
            time = t + time.subSequence(2, 8).toString() + " PM"
        } else {
            time = time + " AM"
        }
        holder.dateTextView.setText(time + " | " + date)

        holder.amount.setText("₹ "+Object.amount)
        holder.paymentMode.setText(Object.balance)
    }

    class CustomerOverTimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.id)
        val paymentMode: TextView =view.findViewById(R.id.paymentmode)
        val dateTextView: TextView = view.findViewById(R.id.date)
        val amount: TextView = view.findViewById(R.id.amount)
        val status: TextView = view.findViewById(R.id.status)
    }
}