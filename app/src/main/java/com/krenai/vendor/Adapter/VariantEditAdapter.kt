package com.krenai.vendor.Adapter

import SliderItemDecoration
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PointF.length
import android.util.Log
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
import com.krenai.vendor.Activity.Products.VariantMatrix

import com.krenai.vendor.R
import com.krenai.vendor.model.DataBrand
import com.krenai.vendor.model.DataCategory
import com.krenai.vendor.model.DataOrderDetails
import com.krenai.vendor.model.DataVariantMatrix
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

class VariantEditAdapter(val context: Context?, val itemList: ArrayList<DataVariantMatrix>,val jsonarr:JSONArray,val variantOptions:JSONArray) :
    RecyclerView.Adapter<VariantEditAdapter.VariantEditViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()
    private var sharedPreferencesStatus: SharedPreferences? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VariantEditAdapter.VariantEditViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.variant_edit_custom_row, parent, false)
        sharedPreferencesStatus = context?.getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )

        return VariantEditViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: VariantEditAdapter.VariantEditViewHolder,
        position: Int
    ) {
        var data = itemList[position]
        holder.name.text = data.name
        val variantValuesRequestsFinal=JSONArray()
        val allVariantsData=JSONArray(sharedPreferencesStatus?.getString("allVariantsData",null))
        for(i in 0 until variantOptions.length())
        {
            for(j in 0 until variantOptions.getJSONObject(i).getJSONArray("variantValue").length())
            {
                var Object=JSONObject()
                var VariantRequest=JSONObject()
                val variantValuesRequests=JSONArray()
                VariantRequest.put("isFlag",1)
                VariantRequest.put("name",variantOptions.getJSONObject(i).getString("variant"))
                for(t in 0 until allVariantsData.length())
                {
                    if(allVariantsData.getJSONObject(t).getString("name")==variantOptions.getJSONObject(i).getString("variant"))
                    {
                        VariantRequest.put("id",allVariantsData.getJSONObject(t).getInt("id"))
                        break
                    }
                }
                VariantRequest.put("supplierId",null)
                Object.put("name",variantOptions.getJSONObject(i).getJSONArray("variantValue").getString(j))
                for(k in 0 until variantOptions.getJSONObject(i).getJSONArray("variantValue").length())
                {
                    var Object=JSONObject()
                    Object.put("name",variantOptions.getJSONObject(i).getJSONArray("variantValue").getString(k))
                    variantValuesRequests.put(Object)
                }
                VariantRequest.put("variantValuesRequests",variantValuesRequests)
                Object.put("variantRequest",VariantRequest)
                variantValuesRequestsFinal.put(Object)
            }
        }
        holder.name.setOnClickListener {
            var intent=Intent(context,VariantMatrix::class.java)
            intent.putExtra("position",position)
            intent.putExtra("name",holder.name.text.toString())
            intent.putExtra("sku",data.sku)
            intent.putExtra("barcode",data.barcode)
            intent.putExtra("mrp",data.mrp)
            intent.putExtra("base",data.base)
            intent.putExtra("price",data.price)
            intent.putExtra("min",data.min)
            intent.putExtra("max",data.max)
            intent.putExtra("qty",data.qty)
            intent.putExtra("iWeight",data.itemWeight)
            intent.putExtra("pWeight",data.packagingWeight)
            intent.putExtra("variantValuesRequests",variantValuesRequestsFinal.toString())
            Log.i("variantValueRequests",variantValuesRequestsFinal.toString())
            Log.i("variantOptions",variantOptions.toString())
            context?.startActivity(intent)
        }
    }

    class VariantEditViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name= view.findViewById(R.id.text) as TextView
    }
}