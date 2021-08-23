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

class ProductsListingAdapter(val context: Context, val itemList: ArrayList<ProductModel?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() , Filterable {

    private val viewPool = RecyclerView.RecycledViewPool()
    var VIEW_ITEM = 1
    var VIEW_PROG = 2
    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""

    private var filteredList=itemList

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
                .inflate(R.layout.products_custom_row, parent, false)
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

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ProductUpdate::class.java)
                val editor = sharedPreferencesStatus!!.edit()
                editor.putString("ProductName",currentProduct?.name.toString())
                editor.putString("ProductDescription",currentProduct?.description.toString())
                editor.putString("ProductCategory",currentProduct?.category.toString())
                editor.putString("ProductID",currentProduct?.id.toString())
                editor.apply()
                intent.putExtra("url",currentProduct?.url)
                intent.putExtra("i",currentProduct?.i)
                var List= ArrayList<String>()
               /* for (i in 0 until (currentProduct?.variantList?.size) as Int)
                {
                    var list= arrayListOf<String>()
                    list.add(currentProduct.variantList[i].name)
                    list.add(currentProduct.variantList[i].sku)
                    list.add(currentProduct.variantList[i].barcode)
                    list.add(currentProduct.variantList[i].price)
                    list.add(currentProduct.variantList[i].mrp)
                    list.add(currentProduct.variantList[i].base)
                    list.add(currentProduct.variantList[i].itemWeight)
                    list.add(currentProduct.variantList[i].packagingWeight)
                    list.add(currentProduct.variantList[i].min)
                    list.add(currentProduct.variantList[i].max)
                    list.add(currentProduct.variantList[i].qty)
                    List.add(list.toString())
                }*/
                /*intent.putExtra("variantSku",currentProduct?.variantList?.sku)
                intent.putExtra("variantBar",currentProduct?.variantList?.barcode)
                intent.putExtra("variantPrice",currentProduct?.variantList?.price)
                intent.putExtra("variantMrp",currentProduct?.variantList?.mrp)
                intent.putExtra("variantBase",currentProduct?.variantList?.base)
                intent.putExtra("variantItemWeight",currentProduct?.variantList?.itemWeight)
                intent.putExtra("variantPackagingWeight",currentProduct?.variantList?.packagingWeight)
                intent.putExtra("variantMin",currentProduct?.variantList?.min)
                intent.putExtra("variantMax",currentProduct?.variantList?.max)
                intent.putExtra("variantQty",currentProduct?.variantList?.qty)  */
                context.startActivity(intent)
            }
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
                adapter = ProductsListingImageAdapter(filteredList.get(position) as ProductModel)
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
                    val resultsModel: MutableList<ProductModel?> = ArrayList()
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
                {   filteredList= results.values as ArrayList<ProductModel?>
                    notifyDataSetChanged()
                }

                }

            }
        }
    }
