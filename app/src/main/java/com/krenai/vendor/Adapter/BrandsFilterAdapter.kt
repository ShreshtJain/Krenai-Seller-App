package com.krenai.vendor.Adapter

import SliderItemDecoration
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

import com.krenai.vendor.R
import com.krenai.vendor.model.DataBrand
import com.krenai.vendor.model.DataCategory
import com.krenai.vendor.model.DataOrderDetails
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.category_filter_row.view.*
import kotlinx.android.synthetic.main.products_custom_row.view.*
import org.json.JSONException
import org.json.JSONObject
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BrandsFilterAdapter(val context: Context?, val itemList: ArrayList<DataBrand>) :
    RecyclerView.Adapter<BrandsFilterAdapter.BrandsFilterViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()
    private val brand_checkedlist= mutableSetOf<String>()
    private var sharedPreferencesStatus: SharedPreferences? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BrandsFilterAdapter.BrandsFilterViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.brand_filter_row, parent, false)
        sharedPreferencesStatus = context?.getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        return BrandsFilterViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: BrandsFilterAdapter.BrandsFilterViewHolder,
        position: Int
    ) {
        val data = itemList[position]

        holder.name.text = data.name
        holder.itemView.checkBox.setOnClickListener {
            val editor = sharedPreferencesStatus!!.edit()
            if ((holder.itemView.checkBox).isChecked())
            {

             brand_checkedlist.add(data.id)
                editor.putStringSet("brand_checkedlist", brand_checkedlist)
                editor.apply()
            }
            else
            {
                brand_checkedlist.remove(data.id)
                editor.putStringSet("brand_checkedlist", brand_checkedlist)
                editor.apply()
            }

            //Log.i("data size 2",subcategorieslisting.toString())
        }
    }

    class BrandsFilterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name= view.findViewById(R.id.checkBox) as CheckBox
    }
}