package com.krenai.vendor.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.krenai.vendor.Activity.Home.CustomerDetails
import com.krenai.vendor.Activity.Orders.Order_Details

import com.krenai.vendor.R
import com.krenai.vendor.model.Dataorders
import org.json.JSONObject
import kotlin.collections.ArrayList

class CustomerAddressAdapter(val context: Context, val itemList: ArrayList<JSONObject>) :
    RecyclerView.Adapter<CustomerAddressAdapter.CustomerOverTimeViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):CustomerAddressAdapter.CustomerOverTimeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.customer_address_custom_row, parent, false)

        return CustomerAddressAdapter.CustomerOverTimeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: CustomerAddressAdapter.CustomerOverTimeViewHolder,
        position: Int
    ) {
        val dataorders = itemList[position]


        holder.addressState.setText(dataorders.getString("addressState"))
        holder.addressCity.setText(dataorders.getString("addressState"))
        holder.addressLine.setText(dataorders.getString("addressLine1")+" "+dataorders.getString("addressLine2"))
        holder.addressPincode.setText(dataorders.getString("pincode"))



    }

    class CustomerOverTimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val addressState: TextView =view.findViewById(R.id.address_state)
        val addressPincode: TextView=view.findViewById(R.id.address_pincode)
        val addressCity: TextView = view.findViewById(R.id.address_city)
        val addressLine: TextView = view.findViewById(R.id.address_line)
    }
}