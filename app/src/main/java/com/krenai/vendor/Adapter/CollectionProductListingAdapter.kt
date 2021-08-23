package com.krenai.vendor.Adapter


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.krenai.vendor.Activity.Products.Create_Product
import com.krenai.vendor.Activity.Products.ProductUpdate
import com.krenai.vendor.R
import com.krenai.vendor.model.DataCollection
import com.krenai.vendor.model.ProductModel
import kotlinx.android.synthetic.main.products_custom_row.view.*

class CollectionProductListingAdapter(val context: Context, val itemList: ArrayList<ProductModel?>) :
    RecyclerView.Adapter<CollectionProductListingAdapter.ProductsListingViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CollectionProductListingAdapter.ProductsListingViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.products_custom_row, parent, false)

        return ProductsListingViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: CollectionProductListingAdapter.ProductsListingViewHolder,
        position: Int
    ) {
            val currentProduct = itemList.get(position)

            holder.name.text = currentProduct?.name
            holder.description.text = currentProduct?.description
            holder.category.text = currentProduct?.category


            val childLayoutManager = LinearLayoutManager(
                holder.itemView.product_image_recycler.context,
                RecyclerView.HORIZONTAL,
                false
            )

            holder.itemView.product_image_recycler.apply {
                layoutManager = childLayoutManager
                adapter = ProductsListingImageAdapter(itemList.get(position) as ProductModel)
                setRecycledViewPool(viewPool)

                val snaphelper: PagerSnapHelper = PagerSnapHelper()
                product_image_recycler.setOnFlingListener(null)
                snaphelper.attachToRecyclerView(product_image_recycler)
                product_image_recycler.setNestedScrollingEnabled(false)
            }
    }


    class ProductsListingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById(R.id.Product_name) as TextView
        val description = view.findViewById(R.id.description) as TextView
        val category: TextView = view.findViewById(R.id.brand) as TextView
    }

}