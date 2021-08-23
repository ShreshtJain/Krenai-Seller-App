package com.krenai.vendor.Adapter


import SliderItemDecoration
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PointF.length
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import com.krenai.vendor.Activity.Products.*
import com.krenai.vendor.Activity.Products.Collections

import com.krenai.vendor.R
import com.krenai.vendor.model.DataCategory
import com.krenai.vendor.model.DataCollection
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.products_custom_row.view.*
import org.json.JSONException
import org.json.JSONObject
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CollectionsListingAdapter(val context: Context, val itemList: ArrayList<DataCollection>) :
    RecyclerView.Adapter<CollectionsListingAdapter.CollectionsListingViewHolder>(), Filterable {

    private val viewPool = RecyclerView.RecycledViewPool()

    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""

    private var filteredList=itemList

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CollectionsListingAdapter.CollectionsListingViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.collection_listing_row, parent, false)
        sharedPreferencesStatus =context.getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")

        return CollectionsListingViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(
        holder: CollectionsListingAdapter.CollectionsListingViewHolder,
        position: Int
    ) {
        val datacollections = filteredList[position]

        holder.name.text=datacollections.name

        if(datacollections.status.toUpperCase()!="NULL")
        {
            holder.status.text=datacollections.status
        }

        if(datacollections.createdDate.toUpperCase()!="NULL")
        {
            val date= datacollections.createdDate.subSequence(0,10).toString()
            var time=datacollections.createdDate.subSequence(11,19).toString()
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
            holder.createdDate.text=date
        }

        Picasso.get().load(datacollections.iconMedia).error(R.drawable.broken_image).into(holder.category_image)

        holder.count.setText(datacollections.count)

        holder.menu.setOnClickListener{
            val popup= PopupMenu(context, holder.menu);
            val inflater: MenuInflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_collection, popup.getMenu())
            popup.show()
            popup.setOnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.add_product -> {
                        val intent=Intent(context, AddProductCollection::class.java)
                        intent.putExtra("collectionId",datacollections.id)

                        context.startActivity(intent)
                        return@setOnMenuItemClickListener true
                    }
                }
                false
            }
        }

        holder.itemView.setOnClickListener{
            val intent=Intent(context,ProductList::class.java)

            intent.putExtra("collectionId",datacollections.id)

            context.startActivity(intent)
        }

    }

    class CollectionsListingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.collection_name) as TextView
        val status=view.findViewById<TextView>(R.id.collection_status)
        val category_image = view.findViewById(R.id.collection_image) as ImageView
        val createdDate=view.findViewById<TextView>(R.id.collection_createdDate)
        val count=view.findViewById<TextView>(R.id.product_count)
        val menu=view.findViewById<ImageView>(R.id.menu_collection)
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
                    val resultsModel: MutableList<DataCollection> = ArrayList()
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
                filteredList= results.values as ArrayList<DataCollection>
                notifyDataSetChanged()
            }
        }
    }
}