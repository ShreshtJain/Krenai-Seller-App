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
import com.krenai.vendor.model.DataCategory
import com.krenai.vendor.model.DataOrderDetails
import com.krenai.vendor.model.DataSubSubCategoryFilter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.category_filter_row.view.*
import kotlinx.android.synthetic.main.products_custom_row.view.*
import org.json.JSONException
import org.json.JSONObject
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class subsubcategoriesFilterAdapter(val context: Context, val itemList: ArrayList<DataSubSubCategoryFilter>) :
    RecyclerView.Adapter<subsubcategoriesFilterAdapter.subsubcategoriesViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()
    private var sharedPreferencesStatus: SharedPreferences? = null
    private val subsubcategory_checkedlist= mutableSetOf<String>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): subsubcategoriesFilterAdapter.subsubcategoriesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.subsubcategories_filter_row, parent, false)
        sharedPreferencesStatus = context.getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        return subsubcategoriesViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: subsubcategoriesFilterAdapter.subsubcategoriesViewHolder,
        position: Int
    ) {
        var data = itemList[position]

        holder.name.text = data.name
        holder.itemView.checkBox.setOnClickListener {
            val editor = sharedPreferencesStatus!!.edit()
            if ((holder.itemView.checkBox).isChecked()) {

                subsubcategory_checkedlist.add(data.id)
                editor.putStringSet("subsubcategory_checkedlist", subsubcategory_checkedlist)
                editor.apply()

            }
            else
            {
                subsubcategory_checkedlist.remove(data.id)
                subsubcategory_checkedlist.add(data.id)
                editor.putStringSet("subsubcategory_checkedlist", subsubcategory_checkedlist)
                editor.apply()
            }
        }
    }

    class subsubcategoriesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name= view.findViewById(R.id.checkBox) as CheckBox
    }
}