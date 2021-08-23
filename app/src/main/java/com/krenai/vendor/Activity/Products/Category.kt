package com.krenai.vendor.Activity.Products

import android.app.SearchManager
import android.content.Context
import android.content.Context.SEARCH_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.krenai.vendor.Activity.RecyclerTouchListener
import com.krenai.vendor.Adapter.CategoriesListingAdapter
import com.krenai.vendor.Adapter.ProductsListingAdapter
import com.krenai.vendor.R
import com.krenai.vendor.model.DataCategory
import com.krenai.vendor.model.Dataproductsimage
import com.krenai.vendor.model.ProductModel
import com.krenai.vendor.utils.jkeys.Keys.CommonResources.CONNECTION_URL_DEMAND
import com.mancj.materialsearchbar.MaterialSearchBar
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class Category : AppCompatActivity() {
    lateinit var errortext: TextView

    lateinit var categories_recycler:RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager
    private  var categorieslisting= arrayListOf<DataCategory>()
    var categoriesListingAdapter = CategoriesListingAdapter(this@Category, categorieslisting)

    var FETCH_CATEGORIES=CONNECTION_URL_DEMAND+"api/v3/stores/categories/supplier/"

    lateinit var clear: Button
    lateinit var fab:FloatingActionButton

    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""

    lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        sharedPreferencesStatus = getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")

        FETCH_CATEGORIES+=userid?.toDouble()?.toInt().toString()+"?"

        searchView = findViewById(R.id.searchBar)

        categories_recycler=findViewById(R.id.categories_recycler)

        errortext=findViewById(R.id.errortext)

        fab=findViewById(R.id.fab)

        setUpRecycler()

        clear=findViewById(R.id.clear)

        val touchListener = RecyclerTouchListener(this,categories_recycler)
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
                            val builder= AlertDialog.Builder(this@Category)
                            builder.setIcon(R.drawable.ic_action_name)
                            builder.setTitle("Attention")
                            builder.setMessage("Are you sure to delete this Category ?")
                            builder.setNegativeButton("No"){dialogInterface, which ->
                            }
                            builder.setPositiveButton("Yes"){dialogInterface, which ->
                                deleteCategory(categorieslisting[position].id,position)
                            }
                            val alertDialog: AlertDialog = builder.create()
                            alertDialog.setCancelable(false)
                            alertDialog.show()
                        }
                    }
                }
            })
        categories_recycler.addOnItemTouchListener(touchListener)
        layoutManager = LinearLayoutManager(this@Category)
        categories_recycler.addItemDecoration(
            DividerItemDecoration( categories_recycler.context, (layoutManager as LinearLayoutManager).orientation)
        )

        clear.setOnClickListener {
            finish()

        }
        fab.setOnClickListener{
            val intent = Intent(this@Category, AddCategory::class.java)
            startActivity(intent)
        }

    }

    fun setUpRecycler()
    {
        val queue = Volley.newRequestQueue(this@Category)

        val shimmerFrameLayout = findViewById(R.id.shimmerLayout) as ShimmerFrameLayout
        shimmerFrameLayout.startShimmer()

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, FETCH_CATEGORIES,
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    val data = response.getJSONArray("object")

                    for (i in 0 until data.length()) {
                        val categoryObject = data.getJSONObject(i)

                        try{
                        val media=categoryObject.getJSONObject("media")

                        val categoryModel = DataCategory(
                            categoryObject.getString("id").toInt(),
                            categoryObject.getString("name"),
                            categoryObject.getString("state"),
                            media.getString("mediaUrl")
                        )
                        categorieslisting.add(categoryModel)
                        }
                        catch (e: JSONException)
                        {
                            val categoryModel = DataCategory(
                                categoryObject.getString("id").toInt(),
                                categoryObject.getString("name"),
                                categoryObject.getString("state"),
                                "null"
                            )
                            categorieslisting.add(categoryModel)
                        }
                    }

                    shimmerFrameLayout.stopShimmer()
                    shimmerFrameLayout.setVisibility(View.GONE)

                    if(categorieslisting.size<1)
                    {
                        errortext.setVisibility(View.VISIBLE)
                        categories_recycler.setVisibility(View.GONE)
                    }
                       categoriesListingAdapter = CategoriesListingAdapter(this@Category, categorieslisting)
                        val mLayoutManager = LinearLayoutManager(this)
                        categories_recycler.layoutManager = mLayoutManager
                        categories_recycler.adapter = categoriesListingAdapter
                        categories_recycler.setHasFixedSize(true)


                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String): Boolean {
                            if (categorieslisting.contains(query)) {
                                categoriesListingAdapter.filter?.filter(query)
                            } else {
                                Toast.makeText(this@Category, "No Match found", Toast.LENGTH_SHORT).show()
                            }
                            return false
                        }
                        override fun onQueryTextChange(newText: String): Boolean {
                            categoriesListingAdapter.filter?.filter(newText)
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
    fun deleteCategory(id: Int,position:Int)
    {
        val url="https://www.outsourcecto.com/api/v3/stores/categories/"+id
        val queue = Volley.newRequestQueue( this)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.DELETE, url,
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    Toast.makeText(this@Category, response.getString("message"), Toast.LENGTH_SHORT).show()

                    if(response.getString("message").equals("Successfully Deleted"))
                    {
                        categorieslisting.removeAt(position);
                        categoriesListingAdapter.notifyItemRemoved(position);
                    }
                    categoriesListingAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError? ->
              Toast.makeText(this@Category, "Some Error Occurred", Toast.LENGTH_SHORT).show()
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
