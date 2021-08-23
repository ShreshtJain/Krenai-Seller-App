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
import com.krenai.vendor.model.DataOrderDetails
import com.krenai.vendor.model.DataSubCategoryFilter
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

@Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
class subcategoriesFilterAdapter(val context: Context, val itemList: ArrayList<DataSubCategoryFilter>) :
    RecyclerView.Adapter<subcategoriesFilterAdapter.subcategoriesFilterViewHolder>() {
    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""
    var checkedlist= arrayListOf<Int>()
    private val subcategory_checkedlist= mutableSetOf<String>()
    private var subsubcategorieslisting= arrayListOf<DataSubSubCategoryFilter>()
    private val viewPool = RecyclerView.RecycledViewPool()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): subcategoriesFilterAdapter.subcategoriesFilterViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.subcategory_filter_row, parent, false)
        sharedPreferencesStatus = context.getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")
        return subcategoriesFilterViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: subcategoriesFilterAdapter.subcategoriesFilterViewHolder,
        position: Int
    ) {
        var data = itemList[position]

        holder.name.text = data.name
        holder.itemView.checkBox.setOnClickListener {
            val editor = sharedPreferencesStatus!!.edit()
            if ((holder.itemView.checkBox).isChecked())
            {
                checkedlist.add(data.id.toInt())
                subcategory_checkedlist.add(data.id)
                editor.putStringSet("subcategory_checkedlist", subcategory_checkedlist)
                editor.apply()
                fetch_sub_sub(data.id.toInt(),subsubcategorieslisting)
                //Log.i("data size", checkedlist.toString())
            }
            else
            {
                subsubcategorieslisting.clear()
                checkedlist.remove(data.id)
                subcategory_checkedlist.remove(data.id)
                editor.putStringSet("subcategory_checkedlist", subcategory_checkedlist)
                editor.apply()
                if(checkedlist.size<=0)
                {
                    format(subsubcategorieslisting)
                }
                else {
                    for (i in 0 until checkedlist.size) {
                        fetch_sub_sub(checkedlist[i], subsubcategorieslisting)
                        //Log.i("data size 3",subcategorieslisting.toString())
                    }
                    //Log.i("data size",checkedlist.toString())
                }

            }
            //Log.i("data size 2",subcategorieslisting.toString())
        }
    }

    class subcategoriesFilterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name= view.findViewById(R.id.checkBox) as CheckBox
    }
    fun format(array:ArrayList<DataSubSubCategoryFilter>)
    {
        val editor = sharedPreferencesStatus!!.edit()
        val stringset= mutableSetOf<String>()
        val intset=  mutableSetOf<String>()
        if(array.size<=0) {
            editor.putStringSet("stringset2",stringset )
            editor.putStringSet("intset2",intset)
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
            editor.putStringSet("intset2",intset )
            editor.putStringSet("stringset2",stringset )
            editor.apply()
            Log.i("datalistint",intset.toString())
            Log.i("dataliststring",stringset.toString())
        }
    }
    fun fetch_sub_sub(pos:Int,array:ArrayList<DataSubSubCategoryFilter>)
    {
        val FETCH_SUB= "https://www.outsourcecto.com/api/v3/sub-sub-categories/subCategory/"+pos+"/"+userid?.toDouble()?.toInt().toString()
        val queue = Volley.newRequestQueue(context)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, FETCH_SUB,
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    var data = response.getJSONArray("object")
                    for (i in 0 until data.length()) {
                        var subsubcategoryObject = data.getJSONObject(i)
                        var model= DataSubSubCategoryFilter(subsubcategoryObject.getString("name")
                            ,subsubcategoryObject.getString("id"))
                        array.add(model)
                    }

                    format(subsubcategorieslisting)
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