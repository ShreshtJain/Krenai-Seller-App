package com.krenai.vendor.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.krenai.vendor.R
import com.krenai.vendor.model.AddProductModel
import com.krenai.vendor.model.ProductModel
import com.krenai.vendor.model.Dataproductsimage
import com.squareup.picasso.Picasso

class AddProductListingImageAdapter( val itemList: AddProductModel) :
    RecyclerView.Adapter<AddProductListingImageAdapter.ProductsListingImageViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddProductListingImageAdapter.ProductsListingImageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_image_custom_row, parent, false)

        return ProductsListingImageViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.subData.size
    }

    override fun onBindViewHolder(
        holder: AddProductListingImageAdapter.ProductsListingImageViewHolder,
        position: Int
    ) {


        Picasso.get().load(itemList.subData.get(position).product_image_url).error(R.drawable.broken_image).into(holder.product_image)
    }

    class ProductsListingImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val product_image = view.findViewById(R.id.product_image) as ImageView


    }
}