package com.krenai.vendor.Activity.Products

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.shimmer.ShimmerFrameLayout
import com.krenai.vendor.Activity.RecyclerTouchListener
import com.krenai.vendor.Adapter.AddProductsListingAdapter
import com.krenai.vendor.Adapter.CollectionProductListingAdapter
import com.krenai.vendor.R
import com.krenai.vendor.model.DataVariantMatrix
import com.krenai.vendor.model.Dataproductsimage
import com.krenai.vendor.model.ProductModel
import com.krenai.vendor.utils.jkeys.Keys.CommonResources.CONNECTION_URL_DEMAND
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class ProductList : AppCompatActivity() {

    lateinit var productListRecycler:RecyclerView
    private var productListing = arrayListOf<ProductModel?>()
    var productListingAdapter = CollectionProductListingAdapter(this, productListing)

    lateinit var errortext: TextView
    lateinit var clear: Button
    lateinit var progressBar: ProgressBar

    var DELETE_PRODUCT= CONNECTION_URL_DEMAND+"api/v3/store/collection/product/delete?collectionId="

    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        sharedPreferencesStatus = getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")

        errortext=findViewById(R.id.errortext)
        progressBar=findViewById(R.id.progress_bar)
        clear=findViewById(R.id.clear)
        clear.setOnClickListener{
            finish()
        }

        productListRecycler=findViewById(R.id.recycler_product_list)

        val touchListener = RecyclerTouchListener(this,productListRecycler)
        touchListener.setClickable(object : RecyclerTouchListener.OnRowClickListener {
                override fun onRowClicked(position: Int) {
                }

                override fun onIndependentViewClicked(
                    independentViewID: Int,
                    position: Int
                ) {
                }
            })
            .setSwipeOptionViews(R.id.img_delete)
            .setSwipeable(R.id.rowFG, R.id.rowBG, object : RecyclerTouchListener.OnSwipeOptionsClickListener {
                override fun onSwipeOptionClicked(viewID: Int, position: Int) {
                    when (viewID) {
                        R.id.img_delete -> {
                            val builder= AlertDialog.Builder(this@ProductList)
                            builder.setIcon(R.drawable.ic_action_name)
                            builder.setTitle("Attention")
                            builder.setMessage("Are you sure to delete this Product from this collection ?")
                            builder.setNegativeButton("No"){dialogInterface, which ->
                            }
                            builder.setPositiveButton("Yes"){dialogInterface, which ->
                                delete(productListing[position]?.id,position)
                            }
                            val alertDialog: AlertDialog = builder.create()
                            alertDialog.setCancelable(false)
                            alertDialog.show()
                        }
                    }
                }
            })
        productListRecycler.addOnItemTouchListener(touchListener)

        getProductList()
    }

    fun getProductList()
    {
        val collectionId=getIntent().getIntExtra("collectionId",0)
        val url=CONNECTION_URL_DEMAND+"api/v3/store/collection/product/list/collection/"+collectionId

        val queue = Volley.newRequestQueue(this)
        val shimmerFrameLayout = findViewById(R.id.shimmerLayout) as ShimmerFrameLayout

        shimmerFrameLayout.setVisibility(View.VISIBLE)
        productListRecycler.setVisibility(View.GONE)
        errortext.setVisibility(View.GONE)
        shimmerFrameLayout.startShimmer()

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url,
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    val data = response.getJSONArray("object")

                    for (i in 0 until data.length()) {
                        val productObject=data.getJSONObject(i).getJSONObject("product")
                        val dataproductsimage = arrayListOf<Dataproductsimage>()

                        val productModel = ProductModel(
                            productObject.getInt("id"),
                            productObject.getString("name"),
                            productObject.getString("description"),
                            productObject.getJSONObject("category").getString("name"),
                            dataproductsimage,url,i
                        )
                        productListing.add(productModel)
                    }

                    shimmerFrameLayout.stopShimmer()
                    shimmerFrameLayout.setVisibility(View.GONE)
                    productListRecycler.setVisibility(View.VISIBLE)

                    if(productListing.size<1)
                    {
                        errortext.setVisibility(View.VISIBLE)
                        productListRecycler.setVisibility(View.GONE)
                    }
                    else{
                        errortext.setVisibility(View.GONE)
                        productListRecycler.setVisibility(View.VISIBLE)
                    }

                    productListingAdapter =
                        CollectionProductListingAdapter(this, productListing)

                    val mLayoutManager = LinearLayoutManager(this)

                    productListRecycler.layoutManager = mLayoutManager
                    productListRecycler.adapter = productListingAdapter
                    productListRecycler.setHasFixedSize(true)

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

    private fun delete(id:Int?,position:Int)
    {
        progressBar.visibility=View.VISIBLE

        val collectionId=getIntent().getIntExtra("collectionId",0)
        val url=DELETE_PRODUCT+collectionId+"&productId="+id

        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.DELETE, url,
            Response.Listener<JSONObject>() { response ->
                progressBar.visibility=View.GONE
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show()

                    if(response.getString("message").equals("Successfully Deleted"))
                    {
                       productListing.removeAt(position);
                       productListingAdapter.notifyItemRemoved(position);
                    }
                    productListingAdapter.notifyDataSetChanged()
                }
                catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError? ->
                progressBar.visibility=View.GONE

                Toast.makeText(this, "Some error Occurred", Toast.LENGTH_SHORT).show()
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
