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
import com.google.android.material.button.MaterialButton
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

class ReviewAdapter(val context: Context?, val itemList: ArrayList<ReviewModel>) :
    RecyclerView.Adapter<ReviewAdapter.FinanceSalesViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewAdapter.FinanceSalesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.customer_reviews_customer_row, parent, false)

        return FinanceSalesViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: ReviewAdapter.FinanceSalesViewHolder,
        position: Int
    ) {
        val data = itemList[position]
        holder.date.text = data.date
        holder.name.text=data.name
        holder.Review.text=data.review
        holder.rating.text=data.rating
        if(data.profilePicUrl=="" || data.profilePicUrl.toUpperCase()=="NULL")
        {
            holder.customerImage.visibility=View.GONE
            holder.customerTextImage.visibility=View.VISIBLE
            holder.customerTextImage.setText(data.name[0].toString())
        }
        else
        {
            holder.customerImage.visibility=View.VISIBLE
            holder.customerTextImage.visibility=View.GONE
            Picasso.get().load(data.profilePicUrl).error(R.drawable.broken_image)
                .into(holder.customerImage)
        }
    }

    class FinanceSalesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date= view.findViewById(R.id.date) as TextView
        val name= view.findViewById(R.id.name) as TextView
        val rating= view.findViewById(R.id.rating) as TextView
        val customerImage= view.findViewById(R.id.profile_image_user) as ImageView
        val customerTextImage= view.findViewById(R.id.text_image) as MaterialButton
        val Review= view.findViewById(R.id.review) as TextView
    }
}