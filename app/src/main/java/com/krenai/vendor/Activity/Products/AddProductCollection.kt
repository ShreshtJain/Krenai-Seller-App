package com.krenai.vendor.Activity.Products

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.shimmer.ShimmerFrameLayout
import com.krenai.vendor.Adapter.AddProductsListingAdapter
import com.krenai.vendor.Adapter.ProductsListingAdapter
import com.krenai.vendor.R
import com.krenai.vendor.model.AddProductModel
import com.krenai.vendor.model.DataVariantMatrix
import com.krenai.vendor.model.Dataproductsimage
import com.krenai.vendor.model.ProductModel
import com.krenai.vendor.utils.jkeys.Keys
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class AddProductCollection : AppCompatActivity() {
    lateinit var clear: Button
    lateinit var save:TextView

    lateinit var progressBar:ProgressBar

    lateinit var searchView: SearchView

    lateinit var errortext: TextView

    private lateinit var productlisting_recycler: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    private var productListing = arrayListOf<AddProductModel?>()
    var productListingAdapter = AddProductsListingAdapter(this, productListing)

    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""

    var scroll=1
    var offset=1

    var Fetch_products= Keys.CommonResources.CONNECTION_URL_DEMAND +"api/v3/stores/products/supplier/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product_collection)

        progressBar=findViewById(R.id.progress_bar)
        searchView = findViewById(R.id.searchBar)
        errortext=findViewById(R.id.errortext)

        productlisting_recycler =findViewById(R.id.ProductListing_recycler)

        save=findViewById(R.id.save)
        save.setOnClickListener {

            progressBar.visibility=View.VISIBLE
            Log.i("list",productListingAdapter.checkedList.toString())
            addProduct(productListingAdapter.checkedList)
        }

        clear=findViewById(R.id.clear)
        clear.setOnClickListener{
            finish()
        }

        sharedPreferencesStatus = getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")

        Fetch_products+=userid?.toDouble()?.toInt().toString()+"?"

        layoutManager = LinearLayoutManager(this)
        productlisting_recycler.onScrollToEnd {
            LoadMore(offset)
        }

        setUpRecycler(offset)
    }

    fun RecyclerView.onScrollToEnd(
        onScrollNearEnd: (Unit) -> Unit
    ) = addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (!productlisting_recycler.canScrollVertically(1)) {
                onScrollNearEnd(Unit)
            }
        }
    })
    fun LoadMore(offsetr: Int) {
        if (scroll == 1) {
            productListing.add(null)
            productlisting_recycler.adapter?.notifyItemInserted(productListing.size - 1)
            scroll = 0
            val queue = Volley.newRequestQueue(this)
            val url = Fetch_products + "currentPage=$offsetr&itemPerPage=12"
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, url,
                Response.Listener<JSONObject>() { response ->
                    /*Once response is obtained, parse the JSON accordingly*/
                    productListing.removeAt(productListing.size - 1)
                    productlisting_recycler.adapter?.notifyItemRemoved(productListing.size)
                    offset+=1
                    try {
                        val data = response.getJSONArray("object")
                        if(data.length()>0) {
                            scroll = 1
                            for (i in 0 until data.length()) {
                                val productObject = data.getJSONObject(i)
                                val productListings = productObject.getJSONArray("productListings")
                                val dataproductsimage = arrayListOf<Dataproductsimage>()
                                val variantList = arrayListOf<DataVariantMatrix>()
                                for (j in 0 until productListings.length()) {
                                    val medias =
                                        productListings.getJSONObject(j).getJSONArray("medias")
                                    val variants = productListings.getJSONObject(j)
                                        .getJSONArray("variantValues")
                                    var s = ""
                                    for (k in 0 until variants.length()) {
                                        s = s + variants.getString(k) + ","
                                    }
                                    s = s.dropLast(1)
                                    val VariantModel = DataVariantMatrix(
                                        s,
                                        productListings.getJSONObject(j).getString("skuCode"),
                                        productListings.getJSONObject(j).getString("barcode"),
                                        productListings.getJSONObject(j).getString("sellingPrice"),
                                        productListings.getJSONObject(j).getString("mrp"),
                                        productListings.getJSONObject(j).getString("purchasePrice"),
                                        productListings.getJSONObject(j).getString("minOrderQty"),
                                        productListings.getJSONObject(j).getString("maxOrderQty"),
                                        productListings.getJSONObject(j).getString("itemWeight"),
                                        productListings.getJSONObject(j)
                                            .getString("packagingWeight"),
                                        productListings.getJSONObject(j)
                                            .getString("inStockQuantity")
                                    )
                                    variantList.add(VariantModel)
                                    for (k in 0 until medias.length()) {
                                        val imagemodel =
                                            Dataproductsimage(
                                                medias.getJSONObject(k).getString("mediaUrl")
                                            )
                                        dataproductsimage.add(imagemodel)
                                    }
                                }
                                val productModel = AddProductModel(
                                    productObject.getInt("id"),
                                    productObject.getString("name"),
                                    productObject.getString("description"),
                                    productObject.getJSONObject("category").getString("name"),
                                    dataproductsimage, url, i,false
                                )
                                productListing.add(productModel)
                            }
                        }
                            productlisting_recycler.adapter?.notifyDataSetChanged()

                        //}
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError? ->
                    //Toast.makeText(activity as? Context, "error", Toast.LENGTH_SHORT).show()
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
    private fun setUpRecycler(offsetr: Int) {
        val queue = Volley.newRequestQueue(this)
        val shimmerFrameLayout = findViewById(R.id.shimmerLayout) as ShimmerFrameLayout
        val url=Fetch_products+"currentPage=$offsetr&itemPerPage=12&isApp=0"

        shimmerFrameLayout.setVisibility(View.VISIBLE)
        productlisting_recycler.setVisibility(View.GONE)
        errortext.setVisibility(View.GONE)
        shimmerFrameLayout.startShimmer()
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url,
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                offset+=1
                try {
                    val data = response.getJSONArray("object")
                    for (i in 0 until data.length()) {
                        val productObject = data.getJSONObject(i)
                        val productListings=productObject.getJSONArray("productListings")
                        val dataproductsimage=arrayListOf<Dataproductsimage>()
                        val variantList= arrayListOf<DataVariantMatrix>()
                        for (j in 0 until productListings.length())
                        {
                            val medias=productListings.getJSONObject(j).getJSONArray("medias")
                            val variants=productListings.getJSONObject(j).getJSONArray("variantValues")

                            var s=""
                            for (k in  0 until variants.length()) {
                                s=s+variants.getString(k)+","
                            }
                            s=s.dropLast(1)
                            val VariantModel=DataVariantMatrix(s,productListings.getJSONObject(j).getString("skuCode"),
                                productListings.getJSONObject(j).getString("barcode"),productListings.getJSONObject(j).getString("sellingPrice"),
                                productListings.getJSONObject(j).getString("mrp"),productListings.getJSONObject(j).getString("purchasePrice"),
                                productListings.getJSONObject(j).getString("minOrderQty"),productListings.getJSONObject(j).getString("maxOrderQty"),
                                productListings.getJSONObject(j).getString("itemWeight"),productListings.getJSONObject(j).getString("packagingWeight"),
                                productListings.getJSONObject(j).getString("inStockQuantity"))
                            variantList.add(VariantModel)
                            for (k in  0 until medias.length()) {
                                val imagemodel =
                                    Dataproductsimage(medias.getJSONObject(k).getString("mediaUrl"))
                                dataproductsimage.add(imagemodel)
                            }
                        }
                        val productModel = AddProductModel(
                            productObject.getInt("id"),
                            productObject.getString("name"),
                            productObject.getString("description"),
                            productObject.getJSONObject("category").getString("name"),
                            dataproductsimage,url,i,false
                        )
                        productListing.add(productModel)
                    }
                    shimmerFrameLayout.stopShimmer()
                    shimmerFrameLayout.setVisibility(View.GONE)
                    productlisting_recycler.setVisibility(View.VISIBLE)
                    if(productListing.size<1)
                    {
                        errortext.setVisibility(View.VISIBLE)
                        productlisting_recycler.setVisibility(View.GONE)
                    }
                    else{
                        errortext.setVisibility(View.GONE)
                        productlisting_recycler.setVisibility(View.VISIBLE)
                    }

                        Log.i("ProductModel",productListing.toString())
                    productListingAdapter =
                            AddProductsListingAdapter(this, productListing)
                        val mLayoutManager = LinearLayoutManager(this)
                        productlisting_recycler.layoutManager = mLayoutManager
                        productlisting_recycler.adapter = productListingAdapter
                        productlisting_recycler.setHasFixedSize(true)

                        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String): Boolean {
                                if (productListing.contains(query)) {
                                    productListingAdapter.filter?.filter(query)
                                } else {
                                    Toast.makeText(this@AddProductCollection, "No Match found", Toast.LENGTH_SHORT).show()
                                }
                                return false
                            }
                            override fun onQueryTextChange(newText: String): Boolean {
                                productListingAdapter.filter?.filter(newText)
                                return false
                            }
                        })


                    //}
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
    fun addProduct(list: ArrayList<Int?>)
    {
        val collectionId=getIntent().getIntExtra("collectionId",0)
        val jsonParams=JSONObject("{ \"collection\" :$collectionId ,\n" +
                "\"productIdList\":$list}")

        val url="https://www.outsourcecto.com/api/v3/store/collection/product/add"

        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST,url, jsonParams,
                Response.Listener {
                    progressBar.visibility=View.GONE
                    try {

                        Toast.makeText(this, it.getString("message"), Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {

                        Toast.makeText(
                            this,
                            "Incorrect Response!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    progressBar.visibility=View.GONE

                    Toast.makeText(this@AddProductCollection, it.message, Toast.LENGTH_SHORT).show()

                }) {
                override fun getHeaders(): Map<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["Authorization"] = " Bearer "+token

                    return headers
                }
            }
        queue.add(jsonObjectRequest)
    }
}
