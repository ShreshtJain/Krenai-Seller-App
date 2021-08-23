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

class CreditAdapter(val context: Context, val itemList: ArrayList<DataCreditModel>) :
    RecyclerView.Adapter<CreditAdapter.CustomerOverTimeViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):CreditAdapter.CustomerOverTimeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.credit_custom_row, parent, false)

        return CreditAdapter.CustomerOverTimeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: CreditAdapter.CustomerOverTimeViewHolder,
        position: Int
    ) {
        val Object = itemList[position]

        holder.transactionId.setText(Object.transactionId)

        holder.typeTextView.setText(Object.type)

        if (Object.type.toUpperCase().equals("DEBIT")) {
           holder.typeTextView.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.red)))
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

        holder.amount.setText(Object.amount)
        holder.balance.setText(Object.balance)
    }

    class CustomerOverTimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val transactionId: TextView = view.findViewById(R.id.transaction_id)
        val typeTextView: TextView =view.findViewById(R.id.type)
        val dateTextView: TextView = view.findViewById(R.id.date)
        val amount: TextView = view.findViewById(R.id.amount)
        val balance: TextView = view.findViewById(R.id.balance)
    }
}