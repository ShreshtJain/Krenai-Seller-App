package com.krenai.vendor.Adapter

import SliderItemDecoration
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

import com.krenai.vendor.R
import com.krenai.vendor.model.DataCategory
import com.krenai.vendor.model.DataCategoryFilter
import com.krenai.vendor.model.DataOrderDetails
import com.krenai.vendor.model.DataSubCategoryFilter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.category_filter_row.view.*
import kotlinx.android.synthetic.main.products_custom_row.view.*
import org.json.JSONException
import org.json.JSONObject
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class categoriesFilterAdapter(val context: Context?, val itemList: ArrayList<DataCategoryFilter>) :
    RecyclerView.Adapter<categoriesFilterAdapter.categoriesFilterViewHolder>() {
    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""
    var checkedlist= arrayListOf<Int>()
    var category_checkedlist= mutableSetOf<String>()
    private var subcategorieslisting= arrayListOf<DataSubCategoryFilter>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): categoriesFilterAdapter.categoriesFilterViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_filter_row, parent, false)
        sharedPreferencesStatus = context?.getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")
        return categoriesFilterViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: categoriesFilterAdapter.categoriesFilterViewHolder,
        position: Int
    ) {
        val data = itemList[position]

        holder.name.text = data.name
        holder.itemView.checkBox.setOnClickListener {
           val editor = sharedPreferencesStatus!!.edit()
            if ((holder.itemView.checkBox).isChecked())
            {
                checkedlist.add(data.id)
                category_checkedlist.add(data.id.toString())
                editor.putStringSet("category_checkedlist", category_checkedlist)
                editor.apply()
                fetch_sub(data.id,subcategorieslisting)
                //Log.i("data size", checkedlist.toString())
            }
            else
            {
                subcategorieslisting.clear()
                checkedlist.remove(data.id)
                category_checkedlist.remove(data.id.toString())
                editor.putStringSet("category_checkedlist", category_checkedlist)
                editor.apply()
                if(checkedlist.size<=0)
                {
                    format(subcategorieslisting)
                }
                else {
                    for (i in 0 until checkedlist.size) {
                        fetch_sub(checkedlist[i], subcategorieslisting)
                        //Log.i("data size 3",subcategorieslisting.toString())
                    }
                    //Log.i("data size",checkedlist.toString())
                }

            }
           //Log.i("data size 2",subcategorieslisting.toString())
          }
    }

    class categoriesFilterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById(R.id.checkBox) as CheckBox

    }
    fun format(array:ArrayList<DataSubCategoryFilter>)
    {
        val editor = sharedPreferencesStatus!!.edit()
        val stringset= mutableSetOf<String>()
        val intset=  mutableSetOf<String>()
        if(array.size<=0) {
            editor.putStringSet("stringset",stringset )
            editor.putStringSet("intset",intset)
            editor.apply()
            Log.i("datalist1",array.toString())
        }
        else
        {
            for ( i in 0 until array.size)
            {
                stringset.add(array[i].name)
                intset.add(array[i].id)
            }
            editor.putStringSet("intset",intset )
            editor.putStringSet("stringset",stringset )
            editor.apply()
            Log.i("datalistint",intset.toString())
            Log.i("dataliststring",stringset.toString())
        }
    }
    fun fetch_sub(pos:Int,array:ArrayList<DataSubCategoryFilter>)
    {
        val FETCH_SUB= "https://www.outsourcecto.com/api/v3/stores/sub-categories/category/"+pos+"/"+userid?.toDouble()?.toInt().toString()
        val queue = Volley.newRequestQueue(context)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, FETCH_SUB,
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    val data = response.getJSONArray("object")
                    for (i in 0 until data.length()) {
                        val subcategoryObject = data.getJSONObject(i)
                        val model= DataSubCategoryFilter(subcategoryObject.getString("name")
                            ,subcategoryObject.getString("id"))
                        array.add(model)
                    }
                    Log.i("data size 2",subcategorieslisting.toString())
                    format(subcategorieslisting)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError? ->
                //Toast.makeText(activity as Context, "error", Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers =
                    HashMap<String, String>()
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                headers["Authorization"] = " Bearer "+token
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }
    }
