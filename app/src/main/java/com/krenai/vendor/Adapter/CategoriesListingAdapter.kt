package com.krenai.vendor.Adapter


import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog

import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.krenai.vendor.R
import com.krenai.vendor.model.DataCategory
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class CategoriesListingAdapter(val context: Context, var itemList: ArrayList<DataCategory>) : RecyclerView.Adapter<CategoriesListingAdapter.CategoriesListingViewHolder>(),Filterable {

    private val viewPool = RecyclerView.RecycledViewPool()

    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""

    private var filteredList=itemList

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoriesListingAdapter.CategoriesListingViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_listing_row, parent, false)
        sharedPreferencesStatus =context.getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")


        return CategoriesListingViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(
        holder: CategoriesListingAdapter.CategoriesListingViewHolder,
        position: Int
    ) {
        val datacategories = filteredList[position]

        holder.name.text=datacategories.name

        if(datacategories.status.toUpperCase()!="NULL")
        {
            holder.status.text=datacategories.status
        }

        Picasso.get().load(datacategories.iconMedia).error(R.drawable.broken_image).into(holder.category_image)

    }

    class CategoriesListingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.category_name) as TextView
        val status=view.findViewById<TextView>(R.id.category_status)
        val category_image = view.findViewById(R.id.category_image) as ImageView

    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                val filterResults = FilterResults()

                if (constraint == null || constraint.length == 0) {
                    filterResults.count = itemList.size
                    filterResults.values = itemList
                }
                else {
                    val resultsModel: MutableList<DataCategory> = ArrayList()
                    val searchStr = constraint.toString().toLowerCase()

                    for (itemModel in itemList) {
                        if (itemModel.name.toLowerCase().contains(searchStr)
                        ) {
                            resultsModel.add(itemModel)
                        }
                    }
                        filterResults.count = resultsModel.size
                        filterResults.values = resultsModel

                }
                return filterResults
            }

            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults
            ) {
                filteredList= results.values as ArrayList<DataCategory>
                notifyDataSetChanged()
            }
        }
    }
}