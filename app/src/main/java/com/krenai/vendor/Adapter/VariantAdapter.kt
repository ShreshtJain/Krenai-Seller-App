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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.category_filter_row.view.*
import kotlinx.android.synthetic.main.products_custom_row.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class VariantAdapter(val context: Context?, val itemList: ArrayList<String>,val variantOptions:JSONArray,val new:Int) :
    RecyclerView.Adapter<VariantAdapter.VariantViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()
    private var sharedPreferencesStatus: SharedPreferences? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VariantAdapter.VariantViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.variant_custom_row, parent, false)
        sharedPreferencesStatus = context?.getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        return VariantViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: VariantAdapter.VariantViewHolder,
        position: Int
    ) {
        var data = itemList[position]
        holder.name.text = data
        holder.itemView.setOnClickListener {
            if(new==0) {
                var variantValue = JSONArray()
                variantValue = variantOptions.getJSONObject(position).getJSONArray("variantValue")
                var intent = Intent(context, VariantDetails::class.java)
                intent.putExtra("name", data)
                intent.putExtra("variantValue", variantValue.toString())
                intent.putExtra("variantOptions", variantOptions.toString())
                intent.putExtra("index", position)
                intent.putExtra("New", new)
                context?.startActivity(intent)
            }
            else {
                val variantValue = JSONArray("[]")
                val intent = Intent(context, VariantDetails::class.java)
                intent.putExtra("name", data)
                intent.putExtra("variantValue", variantValue.toString())
                intent.putExtra("variantOptions", variantOptions.toString())
                intent.putExtra("New", new)
                context?.startActivity(intent)
            }
            }
    }

    class VariantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name= view.findViewById(R.id.text) as TextView
    }
}