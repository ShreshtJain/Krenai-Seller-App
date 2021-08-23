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
import kotlin.collections.ArrayList

class CustomerOverTimeAdapter(val context: Context, val itemList: ArrayList<Dataorders>) :
    RecyclerView.Adapter<CustomerOverTimeAdapter.CustomerOverTimeViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):CustomerOverTimeAdapter.CustomerOverTimeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.customers_custom_row, parent, false)

        return CustomerOverTimeAdapter.CustomerOverTimeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: CustomerOverTimeAdapter.CustomerOverTimeViewHolder,
        position: Int
    ) {
        val dataorders = itemList[position]



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
        holder.amount.text = "₹ "+dataorders.Cost
        holder.contact.text = dataorders.Contact
        holder.EmailId.text=dataorders.Orderid
        holder.description.text=dataorders.description

        holder.itemView.setOnClickListener{
            val intent=Intent(context, CustomerDetails::class.java)

            intent.putExtra("name",dataorders.CustomerName)
            intent.putExtra("date",date)
            intent.putExtra("contact",dataorders.Contact)
            intent.putExtra("emailId",dataorders.Orderid)
            intent.putExtra("amount",dataorders.Cost)
            intent.putExtra("description",dataorders.description)
            intent.putExtra("object",dataorders.jsonObject.toString())

            context.startActivity(intent)
        }

    }

    class CustomerOverTimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById(R.id.name) as TextView
        val amount = view.findViewById(R.id.amount) as TextView
        val contact: TextView = view.findViewById(R.id.contact) as TextView
        val EmailId: TextView = view.findViewById(R.id.orderid) as TextView
        val description=view.findViewById<TextView>(R.id.description)
        val date_order=view.findViewById<TextView>(R.id.date_order)
    }
}