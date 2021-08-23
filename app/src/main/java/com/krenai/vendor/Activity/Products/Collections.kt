package com.krenai.vendor.Activity.Products

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.krenai.vendor.Adapter.CollectionsListingAdapter
import com.krenai.vendor.Adapter.ProductsListingAdapter
import com.krenai.vendor.R
import com.krenai.vendor.model.DataCategory
import com.krenai.vendor.model.DataCollection
import com.krenai.vendor.model.Dataproductsimage
import com.krenai.vendor.model.ProductModel
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class Collections : AppCompatActivity() {

    lateinit var errortext: TextView

    lateinit var collections_recycler:RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager
    private  var collectionslisting= arrayListOf<DataCollection>()
    var collectionsListingAdapter = CollectionsListingAdapter(this@Collections, collectionslisting)

    var FETCH_COLLECTIONS="https://www.outsourcecto.com/api/v3/store/collection/supplier/"

    lateinit var clear: Button
    lateinit var fab:FloatingActionButton

    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""

    lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collections)
        sharedPreferencesStatus = getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")
        FETCH_COLLECTIONS+=userid?.toDouble()?.toInt().toString()+"?"
        collections_recycler=findViewById(R.id.collections_recycler)
        fab=findViewById(R.id.fab)
        errortext=findViewById(R.id.errortext)
        searchView = findViewById(R.id.searchBar)

        setUpRecycler()
        clear=findViewById(R.id.clear)


        layoutManager = LinearLayoutManager(this@Collections)

        val touchListener = RecyclerTouchListener(this,collections_recycler)
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
                            val builder= AlertDialog.Builder(this@Collections)
                            builder.setIcon(R.drawable.ic_action_name)
                            builder.setTitle("Attention")
                            builder.setMessage("Are you sure to delete this Collection ?")
                            builder.setNegativeButton("No"){dialogInterface, which ->
                            }
                            builder.setPositiveButton("Yes"){dialogInterface, which ->
                                deleteCollection(collectionslisting[position].id,position)
                            }
                            val alertDialog: AlertDialog = builder.create()
                            alertDialog.setCancelable(false)
                            alertDialog.show()
                        }
                    }
                }
            })
        collections_recycler.addOnItemTouchListener(touchListener)
        collections_recycler.addItemDecoration(
            DividerItemDecoration

                ( collections_recycler.context, (layoutManager as LinearLayoutManager).orientation)
        )

        clear.setOnClickListener {
            finish()

        }
        fab.setOnClickListener{
            val intent = Intent(this@Collections, AddCollection::class.java)
            startActivity(intent)
        }

    }
    fun setUpRecycler()
    {
        val queue = Volley.newRequestQueue(this@Collections)
        val shimmerFrameLayout = findViewById(R.id.shimmerLayout) as ShimmerFrameLayout
        shimmerFrameLayout.startShimmer()
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, FETCH_COLLECTIONS,
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    val data = response.getJSONArray("object")
                    for (i in 0 until data.length()) {
                        val collectionObject = data.getJSONObject(i)
                        Log.i("id", collectionObject.getString("id").toInt().toString())
                        val collectionModel = DataCollection(
                            collectionObject.getString("id").toInt(),
                            collectionObject.getString("name"),
                            collectionObject.getString("state"),
                            collectionObject.getString("imageUrl"),
                            collectionObject.getString("createdDate"),
                            collectionObject.getString("count")
                        )
                        collectionslisting.add(collectionModel)
                    }
                    shimmerFrameLayout.stopShimmer()
                    shimmerFrameLayout.setVisibility(View.GONE)
                    if(collectionslisting.size<1)
                    {
                        errortext.setVisibility(View.VISIBLE)
                        collections_recycler.setVisibility(View.GONE)
                    }

                    collectionsListingAdapter = CollectionsListingAdapter(this@Collections, collectionslisting)
                    val mLayoutManager = LinearLayoutManager(this)
                    collections_recycler.layoutManager = mLayoutManager
                    collections_recycler.adapter = collectionsListingAdapter
                    collections_recycler.setHasFixedSize(true)


                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String): Boolean {
                            if (collectionslisting.contains(query)) {
                                collectionsListingAdapter.filter?.filter(query)
                            } else {
                                Toast.makeText(this@Collections, "No Match found", Toast.LENGTH_SHORT).show()
                            }
                            return false
                        }
                        override fun onQueryTextChange(newText: String): Boolean {
                            collectionsListingAdapter.filter?.filter(newText)
                            return false
                        }
                    })
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

    fun deleteCollection(id:Int,position:Int)
    {
        val url="https://www.outsourcecto.com/api/v3/store/collection/"+id
        val queue = Volley.newRequestQueue( this)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.DELETE, url,
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show()

                    if(response.getString("message").equals("Successfully Deleted"))
                    {
                        collectionslisting.removeAt(position);
                        collectionsListingAdapter.notifyItemRemoved(position);
                    }
                    collectionsListingAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError? ->
                Toast.makeText(this, "Some Error Occurred", Toast.LENGTH_SHORT).show()
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
