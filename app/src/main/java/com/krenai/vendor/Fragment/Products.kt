package com.krenai.vendor.Fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.krenai.vendor.Activity.Products.Category
import com.krenai.vendor.Activity.Products.Collections
import com.krenai.vendor.Activity.Products.Create_Product
import com.krenai.vendor.Activity.RecyclerTouchListener
import com.krenai.vendor.Adapter.*
import com.krenai.vendor.R
import com.krenai.vendor.model.*
import com.krenai.vendor.utils.jkeys.Keys.CommonResources.CONNECTION_URL_DEMAND
import org.json.JSONException
import org.json.JSONObject
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class Products : Fragment() {
    lateinit var filter:Button
    lateinit var remove_filter:Button
    lateinit var checkbox1:CheckBox
    lateinit var checkbox2:CheckBox
    lateinit var categories_filter_recycler:RecyclerView
    lateinit var subcategories_filter_recycler:RecyclerView
    lateinit var subsubcategories_filter_recycler:RecyclerView
    lateinit var brands_filter_recycler:RecyclerView

    lateinit var searchView: SearchView

    lateinit var errortext: TextView
    lateinit var errortext_category: TextView
    lateinit var errortext_brand: TextView
    lateinit var errortext_subsubcategory: TextView
    lateinit var errortext_subcategory: TextView

    lateinit var cat:Button
    lateinit var sub:Button
    lateinit var subsub:Button
    lateinit var brands:Button
    lateinit var state:Button

    private lateinit var productlisting_recycler: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>

    private var productListing = arrayListOf<ProductModel?>()
    lateinit var productListingAdapter:ProductsListingAdapter

    var scroll=1
    var offset=1
    var sstate=0
    var sbrand=0
    var scat=0
    var ssub=0
    var ssubsub=0

    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""

    var Fetch_products= CONNECTION_URL_DEMAND+"api/v3/stores/products/supplier/"
    var FETCH_CATEGORIES= CONNECTION_URL_DEMAND+"api/v3/stores/categories/supplier/"
    var FETCH_BRANDS= CONNECTION_URL_DEMAND+"api/v3/stores/brand/"
    var DELETE_PRODUCT= CONNECTION_URL_DEMAND+"api/v3/stores/products/"

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_create -> {
                    val intent = Intent(activity, Create_Product::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_filter -> {
                    mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
                R.id.navigation_category -> {
                    val intent = Intent(activity, Category::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_collection -> {
                    val intent = Intent(activity, Collections::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_products, container, false)

        errortext=v.findViewById(R.id.errortext)
        errortext_brand=v.findViewById(R.id.errortext_brand)
        errortext_category=v.findViewById(R.id.errortext_category)
        productlisting_recycler = v.findViewById(R.id.ProductListing_recycler) as RecyclerView
        categories_filter_recycler=v.findViewById(R.id.categories_filter_recycler)
        subcategories_filter_recycler=v.findViewById(R.id.subcategories_filter_recycler)
        subsubcategories_filter_recycler=v.findViewById(R.id.subsubcategories_filter_recycler)
        brands_filter_recycler=v.findViewById(R.id.brands_filter_recycler)
        errortext_subcategory=v.findViewById(R.id.errortext_subcategory)
        errortext_subsubcategory=v.findViewById(R.id.errortext_subsubcategory)

        searchView = v.findViewById(R.id.searchBar)

        filter=v.findViewById(R.id.filter)
        remove_filter=v.findViewById(R.id.filter_not)

        sharedPreferencesStatus = activity?.getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")

        val editor = sharedPreferencesStatus!!.edit()
        editor.putStringSet("brand_checkedlist",null)
        editor.putStringSet("category_checkedlist",null)
        editor.putStringSet("subcategory_checkedlist",null)
        editor.putStringSet("subsubcategory_checkedlist",null)
        editor.putString("variantJSONArray",null)
        editor.putString("sharedVariantOptions",null)
        editor.putString("ProductName",null)
        editor.putString("ProductCategory",null)
        editor.putString("ProductDescription",null)
        editor.putString("ProductID",null)
        editor.apply()

        Fetch_products+=userid?.toDouble()?.toInt().toString()+"?"
        FETCH_BRANDS+=userid?.toDouble()?.toInt().toString()+"?"
        FETCH_CATEGORIES+=userid?.toDouble()?.toInt().toString()+"?"

        cat=v.findViewById(R.id.cat)
        sub=v.findViewById(R.id.sub)
        subsub=v.findViewById(R.id.subsub)
        state=v.findViewById(R.id.state)
        brands=v.findViewById(R.id.brands)
        checkbox1=v.findViewById(R.id.checkBox1)
        checkbox2=v.findViewById(R.id.checkBox2)

        AllVariantsData()
        fetch_brand()
        fetch_category()
        setUpRecycler(v,offset)

        layoutManager = LinearLayoutManager(activity)
        productListingAdapter=ProductsListingAdapter(activity as Context , productListing)

        val touchListener = RecyclerTouchListener(activity,productlisting_recycler)
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
                            val builder= AlertDialog.Builder(activity as Context)
                            builder.setIcon(R.drawable.ic_action_name)
                            builder.setTitle("Attention")
                            builder.setMessage("Are you sure to delete this Product ?")
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
        productlisting_recycler.addOnItemTouchListener(touchListener)
        productlisting_recycler.addItemDecoration(
            DividerItemDecoration

                (
                productlisting_recycler.context,
                (layoutManager as LinearLayoutManager).orientation
            )
        )
            productlisting_recycler.onScrollToEnd {
                LoadMore(offset)
        }


        val navigation =
            v.findViewById<View>(R.id.products_navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val bottomSheet: View = v.findViewById(R.id.bottom_sheet)
        bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        state.setOnClickListener {
         if(sstate==0) {
             state.setBackgroundResource(R.drawable.expand_less)
             sstate = 1
             checkbox2.visibility = View.VISIBLE
             checkbox1.visibility = View.VISIBLE
         }
            else
         {
             state.setBackgroundResource(R.drawable.expand_more)
             sstate=0
             checkbox2.visibility = View.GONE
             checkbox1.visibility = View.GONE
         }
        }
        brands.setOnClickListener {
            if(sbrand==0)
        {
            brands_filter_recycler.visibility=View.VISIBLE
            brands.setBackgroundResource(R.drawable.expand_less)
            sbrand=1
        }
        else
        {
            brands_filter_recycler.visibility=View.GONE
            brands.setBackgroundResource(R.drawable.expand_more)
            sbrand=0
        } }
        cat.setOnClickListener {
            if(scat==0)
        {
            categories_filter_recycler.visibility=View.VISIBLE
            cat.setBackgroundResource(R.drawable.expand_less)
            scat=1
            errortext_subcategory.visibility=View.GONE
            subcategories_filter_recycler.visibility=View.GONE
            sub.setBackgroundResource(R.drawable.expand_more)
            ssub=0
            errortext_subsubcategory.visibility=View.GONE
            subsub.setBackgroundResource(R.drawable.expand_more)
            ssubsub=0
        }
        else
        {
            categories_filter_recycler.visibility=View.GONE
            cat.setBackgroundResource(R.drawable.expand_more)
            scat=0
        } }
        sub.setOnClickListener {
            if(ssub==0)
        {
            val subCategoryFilterlist= arrayListOf<DataSubCategoryFilter>()
            sub.setBackgroundResource(R.drawable.expand_less)
            ssub = 1
            errortext_subsubcategory.visibility=View.GONE
            subsubcategories_filter_recycler.visibility=View.GONE
            subsub.setBackgroundResource(R.drawable.expand_more)
            ssubsub=0
            categories_filter_recycler.visibility=View.GONE
            errortext_category.visibility=View.GONE
            cat.setBackgroundResource(R.drawable.expand_more)
            scat=0
            val stringSet=sharedPreferencesStatus?.getStringSet("stringset",null)
            val intSet=sharedPreferencesStatus?.getStringSet("intset",null)
            Log.i("stringset",stringSet.toString())
            if(stringSet==null)
            {
                errortext_subcategory.setVisibility(View.VISIBLE)
                subcategories_filter_recycler.setVisibility(View.GONE)
                Log.i("list1",stringSet.toString())
                Log.i("list2",intSet.toString())
            }
            else if(stringSet.size<=0)
            {
                errortext_subcategory.setVisibility(View.VISIBLE)
                subcategories_filter_recycler.setVisibility(View.GONE)
                Log.i("list1",stringSet.toString())
                Log.i("list2",intSet.toString())
            }
            else
            {
               Log.i("list1",stringSet.toString())
                Log.i("list2",intSet.toString())
                errortext_subcategory.visibility=View.GONE
                subcategories_filter_recycler.setVisibility(View.VISIBLE)

                for(i in 0 until stringSet.size )
                {
                    val model=DataSubCategoryFilter(stringSet.elementAt(i),intSet?.elementAt(i) as String)
                    subCategoryFilterlist.add(model)

                }
                val Adapter =
                   subcategoriesFilterAdapter(activity as Context, subCategoryFilterlist)
                val mLayoutManager = LinearLayoutManager(activity as Context)
               subcategories_filter_recycler.layoutManager = mLayoutManager
                subcategories_filter_recycler.adapter = Adapter
                subcategories_filter_recycler.setHasFixedSize(true)

            }

        }
        else
        {
            subcategories_filter_recycler.setVisibility(View.GONE)
            errortext_subcategory.visibility=View.GONE
            sub.setBackgroundResource(R.drawable.expand_more)
            ssub=0
        }}
        subsub.setOnClickListener {
            if(ssubsub==0)
        {
            val subsubCategoryFilterlist= arrayListOf<DataSubSubCategoryFilter>()
            subsub.setBackgroundResource(R.drawable.expand_less)
            ssubsub = 1
            errortext_subcategory.visibility=View.GONE
            subcategories_filter_recycler.visibility=View.GONE
            sub.setBackgroundResource(R.drawable.expand_more)
            ssub=0
            categories_filter_recycler.visibility=View.GONE
            errortext_category.visibility=View.GONE
            cat.setBackgroundResource(R.drawable.expand_more)
            scat=0
            val stringSet=sharedPreferencesStatus?.getStringSet("stringset2",null)
            val intSet=sharedPreferencesStatus?.getStringSet("intset2",null)
            if(stringSet==null )
            {
                errortext_subsubcategory.setVisibility(View.VISIBLE)
                subsubcategories_filter_recycler.setVisibility(View.GONE)
                Log.i("list1",stringSet.toString())
                Log.i("list2",intSet.toString())
            }
            else if(stringSet.size<=0 )
            {
                errortext_subsubcategory.setVisibility(View.VISIBLE)
                subsubcategories_filter_recycler.setVisibility(View.GONE)
                Log.i("list1",stringSet.toString())
                Log.i("list2",intSet.toString())
            }
            else
            {
                Log.i("list1",stringSet.toString())
                Log.i("list2",intSet.toString())
                errortext_subsubcategory.visibility=View.GONE
                subsubcategories_filter_recycler.setVisibility(View.VISIBLE)

                for(i in 0 until stringSet.size)
                {
                    val model=DataSubSubCategoryFilter(stringSet.elementAt(i),intSet?.elementAt(i) as String)
                    subsubCategoryFilterlist.add(model)

                }
                val Adapter =
                    subsubcategoriesFilterAdapter(activity as Context, subsubCategoryFilterlist)
                val mLayoutManager = LinearLayoutManager(activity as Context)
                subsubcategories_filter_recycler.layoutManager = mLayoutManager
                subsubcategories_filter_recycler.adapter = Adapter
                subsubcategories_filter_recycler.setHasFixedSize(true)

            }
        }
        else
        {
            subsubcategories_filter_recycler.setVisibility(View.GONE)
            errortext_subsubcategory.visibility=View.GONE
            subsub.setBackgroundResource(R.drawable.expand_more)
            ssubsub=0
        }}
    filter.setOnClickListener {
        filter.setBackgroundResource(R.color.colorAccent)
        filter.setTextColor(Color.WHITE)
        remove_filter.setBackgroundResource(R.drawable.button_blue_hollow_rectangular)
        remove_filter.setTextColor(Color.BLACK)

        val brandset = sharedPreferencesStatus?.getStringSet("brand_checkedlist", null)
        val subcatset = sharedPreferencesStatus?.getStringSet("subcategory_checkedlist", null)
        val subsubcatset = sharedPreferencesStatus?.getStringSet("subsubcategory_checkedlist", null)
        val catset = sharedPreferencesStatus?.getStringSet("category_checkedlist", null)

        Log.i("brand_checkedlist",brandset.toString())
        Log.i("subcategory_checkedlist",subcatset.toString())
        Log.i("subsubcategory",subsubcatset.toString())
        Log.i("category_checkedlist",catset.toString())

        Fetch_products =
            "https://www.outsourcecto.com/api/v3/stores/products/supplier/"+userid?.toDouble()?.toInt().toString()+"?"

        if(catset!=null)
        {
            if(catset.size>0)
            {
                val cat="categoryIds="
                var cat2=""
               for(i in 0 until catset.size)
               {
                   cat2+=cat+catset.elementAt(i)+"&"
               }
                Fetch_products+=cat2
                if(subcatset!=null)
                {
                    if(subcatset.size>0)
                    {
                        val subcat="subCategoryIds="
                        var subcat2=""
                        for(i in 0 until subcatset.size)
                        {
                            subcat2+=subcat+subcatset.elementAt(i)+"&"
                        }
                        Fetch_products+=subcat2
                        if(subsubcatset!=null)
                        {
                            if(subsubcatset.size>0)
                            {
                                val subsubcat="subSubCategoryIds"
                                var subsubcat2=""
                                for(i in 0 until subsubcatset.size)
                                {
                                    subsubcat2+=subsubcat+subsubcatset.elementAt(i)+"&"
                                }
                                Fetch_products+=subsubcat2
                            }
                        }
                    }
                }
            }
        }
        if(brandset!=null)
        {
            if(brandset.size>0)
            {
                val brand="brandIds="
                var brand2=""
                for(i in 0 until brandset.size)
                {
                    brand2+=brand+brandset.elementAt(i)+"&"
                }
                Fetch_products+=brand2
            }
        }
        Log.i("urlfilter",Fetch_products)
        productListing.clear()
        offset=1
        setUpRecycler(v,offset)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }
        remove_filter.setOnClickListener {
            Fetch_products =
                "https://www.outsourcecto.com/api/v3/stores/products/supplier/"+userid?.toDouble()?.toInt().toString()+"?"
            productListing.clear()
            offset=1
            setUpRecycler(v,offset)
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            remove_filter.setBackgroundResource(R.color.colorAccent)
            remove_filter.setTextColor(Color.WHITE)
            filter.setBackgroundResource(R.drawable.button_blue_hollow_rectangular)
            filter.setTextColor(Color.BLACK)
        }
        return v
    }

    private fun setUpRecycler(view: View, offsetr: Int) {
        val queue = Volley.newRequestQueue(activity as Context)
        val shimmerFrameLayout = view.findViewById(R.id.shimmerLayout) as ShimmerFrameLayout
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
                    if (activity != null) {
                        Log.i("ProductModel",productListing.toString())
                      productListingAdapter =
                            ProductsListingAdapter(activity as Context, productListing)
                        val mLayoutManager = LinearLayoutManager(activity)
                        productlisting_recycler.layoutManager = mLayoutManager
                        productlisting_recycler.adapter = productListingAdapter
                        productlisting_recycler.setHasFixedSize(true)

                        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String): Boolean {
                                if (productListing.contains(query)) {
                                    productListingAdapter.filter?.filter(query)
                                } else {
                                    Toast.makeText(context, "No Match found", Toast.LENGTH_SHORT).show()
                                }
                                return false
                            }
                            override fun onQueryTextChange(newText: String): Boolean {
                                productListingAdapter.filter?.filter(newText)
                                return false
                            }
                        })

                    }
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

    fun fetch_category()
    {
        val queue = Volley.newRequestQueue(activity as Context)
        val categorieslisting= arrayListOf<DataCategoryFilter>()
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, FETCH_CATEGORIES,
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    val data = response.getJSONArray("object")
                    for (i in 0 until data.length()) {
                        val categoryObject = data.getJSONObject(i)
                        categorieslisting.add(DataCategoryFilter(categoryObject.getString("name")
                            ,categoryObject.getInt("id")))
                    }
                    if(categorieslisting.size<1)
                    {
                        errortext_category.setVisibility(View.VISIBLE)
                        categories_filter_recycler.setVisibility(View.GONE)
                    }
                    val categoriesFilterAdapter =
                        categoriesFilterAdapter(activity as? Context, categorieslisting)
                    val mLayoutManager = LinearLayoutManager(activity as? Context)
                    categories_filter_recycler.layoutManager = mLayoutManager
                    categories_filter_recycler.adapter = categoriesFilterAdapter
                    categories_filter_recycler.setHasFixedSize(true)
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
    fun fetch_brand()
    {
        val queue = Volley.newRequestQueue(activity as Context)
        val brandslisting= arrayListOf<DataBrand>()
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, FETCH_BRANDS,
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    val data = response.getJSONArray("object")
                    for (i in 0 until data.length()) {
                        val Object = data.getJSONObject(i)
                        brandslisting.add(DataBrand(Object.getString("name"),Object.getString("id")))
                    }
                    if(brandslisting.size<1)
                    {
                        errortext_brand.setVisibility(View.VISIBLE)
                        brands_filter_recycler.setVisibility(View.GONE)
                    }
                    val Adapter =
                        BrandsFilterAdapter(activity as? Context, brandslisting)
                    val mLayoutManager = LinearLayoutManager(activity as? Context)
                    brands_filter_recycler.layoutManager = mLayoutManager
                    brands_filter_recycler.adapter = Adapter
                    brands_filter_recycler.setHasFixedSize(true)
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
            val queue = Volley.newRequestQueue(activity as Context)
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
                                val productModel = ProductModel(
                                    productObject.getInt("id"),
                                    productObject.getString("name"),
                                    productObject.getString("description"),
                                    productObject.getJSONObject("category").getString("name"),
                                    dataproductsimage, url, i
                                )
                                productListing.add(productModel)
                            }
                        }
                        if (activity != null) {
                            productlisting_recycler.adapter?.notifyDataSetChanged()
                        }
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
    fun AllVariantsData()
    {
        val queue = Volley.newRequestQueue(activity as Context)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, "https://www.outsourcecto.com/api/v3/stores/variant/all",
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    val allVariantsData = response.getJSONArray("object")
                    val editor = sharedPreferencesStatus!!.edit()
                    editor.putString("allVariantsData",allVariantsData.toString())
                    editor.apply()
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
        val url=DELETE_PRODUCT+id
        Log.i("delete url",url)

        val queue = Volley.newRequestQueue( context)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.DELETE, url,
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show()

                    if(response.getString("message").equals("Successfully Deleted"))
                    {
                        productListing.removeAt(position);
                        productListingAdapter.notifyItemRemoved(position);
                    }
                    productListingAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError? ->
                Toast.makeText(context, "Some error Occurred", Toast.LENGTH_SHORT).show()
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


