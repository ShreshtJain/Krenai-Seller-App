package com.krenai.vendor.Adapter


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.krenai.vendor.Activity.Products.Create_Product
import com.krenai.vendor.Activity.Products.ProductUpdate
import com.krenai.vendor.R
import com.krenai.vendor.model.AddProductModel
import com.krenai.vendor.model.DataCollection
import com.krenai.vendor.model.ProductModel
import kotlinx.android.synthetic.main.add_products_collection_custom_row.view.*
import kotlinx.android.synthetic.main.category_filter_row.view.*
import kotlinx.android.synthetic.main.products_custom_row.view.*
import kotlinx.android.synthetic.main.products_custom_row.view.product_image_recycler

class AddProductsListingAdapter(val context: Context, val itemList: ArrayList<AddProductModel?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() , Filterable {

    private val viewPool = RecyclerView.RecycledViewPool()
    var VIEW_ITEM = 1
    var VIEW_PROG = 2
    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""

    private var filteredList=itemList
    var checkedList= arrayListOf<Int?>()

    override fun getItemViewType(position: Int): Int {
        if(itemList.get(position)!=null){
            return  VIEW_ITEM
        }
        else{
            return VIEW_PROG
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        lateinit var vh:RecyclerView.ViewHolder
        if(viewType==VIEW_ITEM){
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.add_products_collection_custom_row, parent, false)
            vh=ProductsListingViewHolder(itemView)
        }
        else{
            val itemView= LayoutInflater.from(parent.context)
                .inflate(R.layout.progressbar, parent, false)
            vh=ProgressViewHolder(itemView)}
        sharedPreferencesStatus = context.getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid = sharedPreferencesStatus?.getString("USER_ID", "")
        token = sharedPreferencesStatus?.getString("TOKEN", "")
        return vh
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if(holder is ProductsListingViewHolder) {
            val currentProduct = filteredList.get(position)


            holder.name.text = currentProduct?.name
            holder.description.text = currentProduct?.description
            holder.category.text = currentProduct?.category

            if (currentProduct?.checked==true) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }

            holder.checkBox.setOnClickListener {
                if ((holder.checkBox).isChecked())
                {
                    holder.checkBox.setChecked(true);
                    checkedList.add(currentProduct?.id)
                    currentProduct?.checked=true
                }
                else
                {
                    holder.checkBox.setChecked(false);
                    checkedList.remove(currentProduct?.id)
                    currentProduct?.checked=false
                }
            }

            val childLayoutManager = LinearLayoutManager(
                holder.itemView.product_image_recycler.context,
                RecyclerView.HORIZONTAL,
                false
            )

            holder.itemView.product_image_recycler.apply {
                layoutManager = childLayoutManager
                adapter = AddProductListingImageAdapter(filteredList.get(position) as AddProductModel)
                setRecycledViewPool(viewPool)

                val snaphelper: PagerSnapHelper = PagerSnapHelper()
                product_image_recycler.setOnFlingListener(null)
                snaphelper.attachToRecyclerView(product_image_recycler)
                product_image_recycler.setNestedScrollingEnabled(false)
            }
        }
        else
        {
            val h=(holder as ProgressViewHolder)
            h.progressBar
        }


    }


    class ProductsListingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById(R.id.Product_name) as TextView
        val description = view.findViewById(R.id.description) as TextView
        val category: TextView = view.findViewById(R.id.brand) as TextView
        val checkBox= view.findViewById(R.id.check_box) as CheckBox
    }
    class ProgressViewHolder(view: View):RecyclerView.ViewHolder(view){
        val progressBar=view.findViewById<ProgressBar>(R.id.progressbar)
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
                    val resultsModel: MutableList<AddProductModel?> = ArrayList()
                    val searchStr = constraint.toString().toLowerCase()

                    for (itemModel in itemList) {
                        if (itemModel?.name?.toLowerCase()?.contains(searchStr) as Boolean) {
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
                if(results.values!=null)
                {   filteredList= results.values as ArrayList<AddProductModel?>
                    notifyDataSetChanged()
                }

            }

        }
    }
}
