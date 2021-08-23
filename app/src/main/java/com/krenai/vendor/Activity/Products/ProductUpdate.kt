package com.krenai.vendor.Activity.Products

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.krenai.vendor.Adapter.VariantEditAdapter
import com.krenai.vendor.R
import com.krenai.vendor.model.DataVariantMatrix
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class ProductUpdate : AppCompatActivity() {
    lateinit var progressLinearLayout: LinearLayout
    lateinit var menu_variant:ImageView
    lateinit var variantCardView:CardView
    lateinit var hsn:EditText
    lateinit var tax:EditText
    lateinit var thresholdQty:EditText
    lateinit var variant_checkbox:CheckBox
    lateinit var checked_variant:CardView
    lateinit var unchecked_variant:CardView
    private lateinit var recycler: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    private var clear: Button? = null
    lateinit var title:EditText
    lateinit var desc:EditText
    lateinit var brand:EditText
    lateinit var save:TextView
    private var mediaId:Int?=null
    lateinit var upload_image: ImageView
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseuri:String?=null
    private lateinit var auth: FirebaseAuth
    private  var firebaseUser: FirebaseUser?=null
    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""
    lateinit var sku:EditText
    lateinit var max:EditText
    lateinit var min:EditText
    lateinit var price:EditText
    lateinit var base:EditText
    lateinit var barcode:EditText
    lateinit var item_weight:EditText
    lateinit var qty:EditText
    lateinit var packaging_weight:EditText
    lateinit var mrp:EditText
    lateinit var imageCardView:CardView
    lateinit var addVariantButton:TextView
    var variantValues=JSONArray()
    var variantsOptions=JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_update)
        sharedPreferencesStatus = getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        menu_variant=findViewById(R.id.menu_variant)
        addVariantButton=findViewById(R.id.add_variant)
        addVariantButton.setOnClickListener {
            val intent=Intent(this,AddVariants::class.java)
            intent.putExtra("variantOptions",variantsOptions.toString())
            startActivity(intent)
        }
        menu_variant.setOnClickListener {
            val popup= PopupMenu(this, menu_variant);
            val inflater:MenuInflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_variants, popup.getMenu())
            popup.show()
            popup.setOnMenuItemClickListener { item ->
               when(item.itemId) {
                      R.id.action_edit_options -> {
                        val intent=Intent(this,EditVariantOptions::class.java)
                          intent.putExtra("variantOptions",variantsOptions.toString())
                          startActivity(intent)
                        return@setOnMenuItemClickListener true
                    }
            }
                false
        }
        }
        progressLinearLayout=findViewById(R.id.progress_linear_layout)
        variantCardView=findViewById(R.id.variantCardView)
        imageCardView=findViewById(R.id.imageCardView)
        hsn=findViewById(R.id.hsn)
        tax=findViewById(R.id.tax)
        thresholdQty=findViewById(R.id.threshold)
        variant_checkbox=findViewById(R.id.variant_checkbox)
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")
        userid=userid?.toDouble()?.toInt().toString()
        clear = findViewById(R.id.clear)
        barcode=findViewById(R.id.barcode)
        sku=findViewById(R.id.sku)
        packaging_weight=findViewById(R.id.packaging_weight)
        item_weight=findViewById(R.id.edit_item_weight)
        price=findViewById(R.id.price)
        mrp=findViewById(R.id.mrp)
        max=findViewById(R.id.max)
        min=findViewById(R.id.min)
        qty=findViewById(R.id.qty)
        base=findViewById(R.id.base)
        title=findViewById(R.id.title)
        desc=findViewById(R.id.description)
        brand=findViewById(R.id.brand)
        upload_image=findViewById(R.id.update_image)
        brand.setText(sharedPreferencesStatus?.getString("ProductCategory",null))
        title.setText(sharedPreferencesStatus?.getString("ProductName",null))
        desc.setText(sharedPreferencesStatus?.getString("ProductDescription",null))
        save=findViewById(R.id.save)
        recycler=findViewById(R.id.variant_edit_recycler)
        checked_variant=findViewById(R.id.variant_checked)
        unchecked_variant=findViewById(R.id.unchecked_variant)
        layoutManager = LinearLayoutManager(this)
        variant_checkbox.setOnClickListener{
            if(variant_checkbox.isChecked)
            {
                unchecked_variant.visibility=View.GONE
                variantCardView.visibility=View.VISIBLE
                imageCardView.visibility=View.GONE
            }
            else
            {
                imageCardView.visibility=View.VISIBLE
                unchecked_variant.visibility=View.VISIBLE
                variantCardView.visibility=View.GONE
            }
        }
        brand.addTextChangedListener{
            val editor = sharedPreferencesStatus!!.edit()
            editor.putString("ProductCategory",brand.text.toString())
            editor.apply()
        }
        desc.addTextChangedListener {
            val editor = sharedPreferencesStatus!!.edit()
            editor.putString("ProductDescription",desc.text.toString())
            editor.apply()
        }
        title.addTextChangedListener{
            val editor = sharedPreferencesStatus!!.edit()
            editor.putString("ProductName",title.text.toString())
            editor.apply()
        }

        clear?.setOnClickListener {
            val editor = sharedPreferencesStatus!!.edit()
            editor.putString("variantJSONArray",null)
            editor.putString("sharedVariantOptions",null)
            editor.putString("ProductName",null)
            editor.putString("ProductCategory",null)
            editor.putString("ProductDescription",null)
            editor.putString("ProductID",null)
            editor.apply()
            finish()
        }
        upload_image.setOnClickListener {
            launchGallery()
        }
        auth = Firebase.auth
        val user: FirebaseUser? = auth.currentUser
        save.setOnClickListener {
            val s = sharedPreferencesStatus?.getString("variantJSONArray", null)
            val productListing = JSONArray(s)
            Log.i("productlist",productListing.toString())
            if(variant_checkbox.isChecked)
            {
                if(variantsOptions.length()<1)
                {
                    Toast.makeText(this,"This Product Doesn't have any variant!!!",Toast.LENGTH_SHORT).show()
                }
                else
                {
                    for(i in 0 until productListing.length())
                    {
                        if(productListing.getJSONObject(i).length()==0)
                        {
                            Toast.makeText(this,"Fill Variant Details ",Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                    }
                    if(hsn.getText().toString().trim().length<1 || thresholdQty.text.toString().trim().length<1 ||
                        title.text.toString().trim().length<1 || tax.text.toString().trim().length<1)
                    {
                        if (hsn.getText().toString().trim().length<1) {
                            hsn.setError("This field can not be blank")
                        }
                        if(thresholdQty.text.toString().trim().length<1){
                            thresholdQty.setError("This field can not be blank")
                        }
                        if(title.text.toString().trim().length<1){
                            title.setError("This field can not be blank")
                        }
                        if(tax.text.toString().trim().length<1){
                            tax.setError("This field can not be blank")

                        }
                        return@setOnClickListener}
                    progressLinearLayout.visibility=View.VISIBLE
                    update(productListing)
                }
            }

            else {
                if(sku.getText().toString().trim().length < 1 || qty.getText().toString().trim().length < 1 ||
                    price.getText().toString().trim().length < 1 ||base.getText().toString().trim().length < 1
                    ||  mrp.getText().toString().trim().length < 1) {
                    if (sku.getText().toString().trim().length < 1) {
                        sku.setError("This field can not be blank")
                    }
                    if (qty.getText().toString().trim().length < 1) {
                        qty.setError("This field can not be blank")
                    }
                    if (price.getText().toString().trim().length < 1) {
                        price.setError("This field can not be blank")
                    }
                    if (mrp.getText().toString().trim().length < 1) {
                        mrp.setError("This field can not be blank")
                    }
                    if (base.getText().toString().trim().length < 1) {
                        base.setError("This field can not be blank")
                    }
                    return@setOnClickListener
                }
                if(hsn.getText().toString().trim().length<1 || thresholdQty.text.toString().trim().length<1 ||
                    title.text.toString().trim().length<1 || tax.text.toString().trim().length<1)
                {
                    if (hsn.getText().toString().trim().length<1) {
                        hsn.setError("This field can not be blank")
                    }
                    if(thresholdQty.text.toString().trim().length<1){
                        thresholdQty.setError("This field can not be blank")
                    }
                    if(title.text.toString().trim().length<1){
                        title.setError("This field can not be blank")
                    }
                    if(tax.text.toString().trim().length<1){
                        tax.setError("This field can not be blank")

                    }
                    return@setOnClickListener}
                if (user != null) {
                    progressLinearLayout.visibility = View.VISIBLE
                    uploadImage(productListing)
                } else {
                    progressLinearLayout.visibility = View.VISIBLE
                    signInAnonymously()
                    uploadImage(productListing)
                }
            }
        }

        recycler.visibility=View.GONE
        if(getIntent().getStringExtra("starter")!=null)
        {
            variantsOptions=JSONArray(getIntent().getStringExtra("variantOptions"))
            setUpVariantRecycler(JSONArray(getIntent().getStringExtra("variantOptions")))

        }
        else
        {
            setUpVariantRecycler()
        }
    }
    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }
            filePath = data.data
            Log.i("Filepath",filePath.toString())
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                upload_image.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun uploadImage(arr:JSONArray){
        if(filePath != null)
        {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val ref = storageRef.child("uploads/" + UUID.randomUUID().toString())
            Log.i("ref",ref.toString())
            Log.i("storageref",storageRef.toString())
            val uploadTask = ref?.putFile(filePath!!)
            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    progressLinearLayout.visibility=View.GONE
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    firebaseuri=downloadUri.toString()
                    addUploadRecordToDb(downloadUri.toString())
                    Log.i("downloaduri",downloadUri.toString())
                    val jsonParams= JSONObject()
                    val jsonArray=JSONArray()
                    val jsonmedia=JSONObject()
                    jsonmedia.put("mediaName", "string")
                    jsonmedia.put("mediaUrl",firebaseuri)
                    jsonmedia.put( "supplierId",userid)
                    jsonmedia.put(  "tags",0)
                    jsonArray.put(jsonmedia)
                    jsonParams.put("medias", jsonArray)
                    Log.i("jsonobject",jsonParams.toString())
                    var url="https://www.outsourcecto.com/api/v3/stores/medias/bulk"
                    val queue = Volley.newRequestQueue(this)
                    val jsonObjectRequest =
                        object : JsonObjectRequest(Method.POST,url, jsonParams,
                            Response.Listener {
                                Log.i("RESPONSEcategoryr",it.toString())
                                try {
                                    Log.i("RESPONSEcategory",it.toString())
                                    // Toast.makeText(this, it.getString("message"), Toast.LENGTH_SHORT).show()
                                    mediaId=it.getJSONArray("object").getJSONObject(0).getInt("id")
                                    jsonParams.getJSONArray("medias").getJSONObject(0).put("id",mediaId)
                                    Log.i("mediaId",mediaId.toString())
                                    jsonmedia.put("id",mediaId)
                                    jsonArray.put(jsonmedia)
                                    updateWithoutVariants(arr,jsonArray)
                                }
                                catch (e: Exception) {
                                    progressLinearLayout.visibility=View.GONE
                                    Toast.makeText(
                                        this,
                                        "Incorrect Media Response!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }, Response.ErrorListener {
                                progressLinearLayout.visibility=View.GONE
                                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()

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
                } else {
                    // Handle failures
                    Toast.makeText(this, "Error! Please try again", Toast.LENGTH_SHORT).show()
                    Log.i("downloaduri","FAILU")
                }
            }?.addOnFailureListener{
                progressLinearLayout.visibility=View.GONE
                Log.i("downloaduri","FAILUre")
                Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
            }
            Log.i("urlTask",urlTask.toString())
        }else{
            progressLinearLayout.visibility=View.GONE
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }
    private fun addUploadRecordToDb(uri: String){
        val db = FirebaseFirestore.getInstance()

        val data = HashMap<String, Any>()
        data["imageUrl"] = uri

        db.collection("posts")
            .add(data)
            .addOnSuccessListener { documentReference ->
                //Toast.makeText(this, "Saved to DB", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                //Toast.makeText(this, "Error saving to DB", Toast.LENGTH_LONG).show()
            }
    }
    fun updateWithoutVariants(arr:JSONArray,medias:JSONArray)
    {
        var Object=arr.getJSONObject(0)
        var variantRequest=JSONObject()
        variantRequest.put("id",5)
        Object.put("thresholdQuantity",thresholdQty.text.toString())
        Object.put("skuCode",sku.text.toString())
        Object.put("sellingPrice",price.text.toString())
        Object.put("mrp",mrp.text.toString())
        Object.put("purchasePrice",base.text.toString())
        Object.put("inStockQuantity",qty.text.toString())
        Object.put("variantValues",null)
        Object.put("barcode",barcode.text.toString())
        Object.put("minOrderQty",min.text.toString())
        Object.put("maxOrderQty",max.text.toString())
        Object.put("itemWeight",item_weight.text.toString())
        Object.put("packagingWeight",packaging_weight.text.toString())
        Object.put("isTaxIncluded",tax.text.toString())
        Object.put("variantRequest",variantRequest)
        Object.put("variantValuesRequests",null)
        Log.i("arr",arr.toString())
        val id = sharedPreferencesStatus?.getString("ProductID",null)
        val json="{\"createdDate\":null,\"updatedDate\":null,\"isFlag\":1,\"createdBy\":null,\"updatedBy\":null," +
                "\"state\":{\"id\":1,\"description\":\"ACTIVE\",\"type\":\"Product\"},\"id\":$id,\"name\":\"${title.text}\",\"description\":\"${desc.text}\"," +
                "\"hsnCode\":\"${hsn.text}\",\"productAttribute\":0,\"brand\":{\"createdDate\":\"2020-10-12T13:38:31.000+0000\"," +
                "\"updatedDate\":\"2020-11-07T15:30:32.000+0000\",\"isFlag\":1,\"createdBy\":null,\"updatedBy\":null,\"state\":null," +
                "\"id\":1841,\"name\":\"9 AM\",\"supplierId\":${userid?.toInt()},\"isFeatured\":1,\"isBannerBrand\":0,\"position\":0,\"supplierBrandId\":0,\"media\":null}," +
                "\"supplierId\":\"${userid?.toInt()}\",\"category\":{\"createdDate\":\"2020-11-17T09:46:12.000+0000\",\"updatedDate\":null,\"isFlag\":1," +
                "\"createdBy\":null,\"updatedBy\":null,\"state\":null,\"id\":557,\"name\":\"diwali\",\"supplierId\":1," +
                "\"description\":\"\",\"position\":0,\"discount\":0,\"visibilityCount\":1,\"iconMedia\":null,\"media\":null," +
                "\"supplierCategoryId\":0,\"productCount\":null},\"subCategory\":null,\"subSubCategory\":null,\"isFeatured\":0,\"gst\":87," +
                "\"variantValues\":[{\"variant\":\"NO VARIANT\",\"variantValue\":[]}]," +
                "\"productListings\":$arr,\"medias\":$medias,\"minOrderQty\":null,\"maxOrderQty\":null," +
                "\"brandName\":null,\"productUniqueIdentifier\":null,\"categoryId\":\"557\"," +
                "\"brandId\":\"1841\",\"mediaMappingRequests\":$medias,\"productListingRequests\":$arr}"
        val j=JSONObject(json)
        val url = "https://www.outsourcecto.com/api/v3/stores/products"
        Log.i("jsonobjectupdateWV", json)
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest =
            object : JsonObjectRequest(Method.PUT, url, j,
                Response.Listener {
                    progressLinearLayout.visibility=View.GONE
                    try {
                        Log.i("RESPONSEcategoryupdate", it.toString())
                        Toast.makeText(this, it.getString("message"), Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {

                        Toast.makeText(
                            this,
                            "Incorrect Response!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    progressLinearLayout.visibility=View.GONE
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()

                }) {
                override fun getHeaders(): Map<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["Authorization"] = " Bearer " + token

                    return headers
                }
            }
        queue.add(jsonObjectRequest)
    }
    fun update(Jsonarray: JSONArray) {
        val medias=JSONArray()
        for(i in 0 until Jsonarray.length())
        {
            val Object=Jsonarray.getJSONObject(i)
            Object.put("thresholdQuantity",thresholdQty.text.toString())
            Object.put("isTaxIncluded",tax.text.toString())
            for(j in 0 until Object.getJSONArray("medias").length())
            {
                medias.put(Object.getJSONArray("medias").getJSONObject(j))
                medias.put(Object.getJSONArray("medias").getJSONObject(j))
            }
        }
        Log.i("inupdate", "insideupdate")
        val id = sharedPreferencesStatus?.getString("ProductID",null)
            val jsonParams2 ="{\"createdDate\": null,\"updatedDate\": null,\"isFlag\": 1,\"createdBy\": null," +
                    "\"updatedBy\": null,\"state\": {\"id\": 1,\"description\": \"ACTIVE\",\"type\":\"Product\"},\"id\": ${id?.toInt()}," +
                    "\"name\": \"${title.text}\",\"description\": " +
                    "\"${desc.text}\"," +
                    "\"hsnCode\": ${hsn.text},\"productAttribute\": 0,\"brand\": {\"createdDate\": " +
                    "\"2020-11-16T17:57:53.000+0000\",\"updatedDate\":\"2020-11-18T12:53:12.000+0000\",\"isFlag\": 1," +
                    "\"createdBy\": null,\"updatedBy\": null,\"state\": null,\"id\": 2208,\"name\": \"Nike\",\"supplierId\": 24," +
                    "\"isFeatured\": 1,\"isBannerBrand\": 0,\"position\": 1,\"supplierBrandId\": 0,\"media\": null  }," +
                    "\"supplierId\": ${userid?.toInt()},\"category\": {\"createdDate\": \"2020-11-16T16:38:58.000+0000\"," +
                    "\"updatedDate\": \"2020-11-18T10:36:48.000+0000\",\"isFlag\": 1,\"createdBy\": null," +
                    "\"updatedBy\": null,\"state\": null,\"id\": 551,\"name\": \"Men\",\"supplierId\": 24," +
                    "\"description\": \"Main Category of mens section\",\"position\": 1,\"discount\": 0," +
                    "\"visibilityCount\": 256,\"iconMedia\": null,\"media\": null,\"supplierCategoryId\": 0," +
                    "\"productCount\": null},\"subCategory\": {\"createdDate\": \"2020-11-16T16:44:06.000+0000\"," +
                    "\"updatedDate\": \"2020-11-17T16:46:47.000+0000\",\"isFlag\": 1,\"createdBy\": null,\"updatedBy\": null," +
                    "\"state\": null,\"id\": 725,\"category\": {\"createdDate\": \"2020-11-16T16:38:58.000+0000\"," +
                    "\"updatedDate\": \"2020-11-18T10:36:48.000+0000\",\"isFlag\": 1,\"createdBy\": null,\"updatedBy\": null," +
                    "\"state\": null,\"id\": 551,\"name\": \"Men\",\"supplierId\": 24," +
                    "\"description\": \"Main Category of mens section\",\"position\": 1,\"discount\": 0,\"visibilityCount\": 256," +
                    "\"iconMedia\": null,\"media\": null,\"supplierCategoryId\": 0,\"productCount\": null},\"name\": \"Top wear\"," +
                    "\"supplierId\": 24,\"discount\": 0,\"position\": 1,\"description\": null,\"visibilityCount\": 173," +
                    "\"iconMedia\": null,\"media\": null,\"supplierSubCategoryId\": 0,\"productCount\": null  }," +
                    "\"subSubCategory\": null,\"isFeatured\": 0,\"gst\": 5,\"variantValues\": []," +
                    "\"productListings\":$Jsonarray,\"medias\":$medias,\"minOrderQty\": null,\"maxOrderQty\": null," +
                    "\"brandName\": null,\"productUniqueIdentifier\": null,\"categoryId\": \"551\",\"subCategoryId\": 725," +
                    "\"brandId\": \"2208\",\"thresholdQuantity\": ${thresholdQty.text},\"variants\": []," +
                    "\"mediaMappingRequests\": [],\"productListingRequests\": $Jsonarray}"


            var j=JSONObject(jsonParams2)
            Log.i("jsonobjectupdate", jsonParams2)
            var url = "https://www.outsourcecto.com/api/v3/stores/products"
            val queue = Volley.newRequestQueue(this)
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.PUT, url, j,
                    Response.Listener {
                        progressLinearLayout.visibility=View.GONE
                        try {
                            Log.i("RESPONSEcategoryupdate", it.toString())
                            Toast.makeText(this, it.getString("message"), Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {

                            Toast.makeText(
                                this,
                                "Incorrect Response!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }, Response.ErrorListener {
//                Toast.makeText(this@Order_Details, it.message, Toast.LENGTH_SHORT).show()
                        progressLinearLayout.visibility=View.GONE

                    }) {
                    override fun getHeaders(): Map<String, String> {
                        val headers =
                            HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        headers["Authorization"] = " Bearer " + token

                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser== null) {
            signInAnonymously()
        }
        else
        {
            firebaseUser=currentUser
        }
    }
    private fun signInAnonymously() {
        // [START signin_anonymously]
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.i("signedin", "signInAnonymously:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.i("signedinfail", "signInAnonymously:failure", task.exception)
                    //Toast.makeText(baseContext, "Authentication failed.",
                        //Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }

                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        // [END signin_anonymously]
    }
    private fun updateUI(user: FirebaseUser?) {
        firebaseUser = user
    }
    fun setUpVariantRecycler()
    {
        val queue = Volley.newRequestQueue(this)
       // var shimmerFrameLayout = view?.findViewById(R.id.shimmerLayout) as ShimmerFrameLayout
        val url=getIntent().getStringExtra("url")
        val pos=getIntent().getIntExtra("i",0)
        //shimmerFrameLayout.setVisibility(View.VISIBLE)
        //productlisting_recycler.setVisibility(View.GONE)
        //errortext.setVisibility(View.GONE)
        //shimmerFrameLayout.startShimmer()
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url,
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
          //      offset+=1
                try {
                    val data = response.getJSONArray("object")
                        val productObject = data.getJSONObject(pos)
                        hsn.setText(productObject.getString("hsnCode"))
                        val productListings=productObject.getJSONArray("productListings")
                      //  var dataproductsimage=arrayListOf<Dataproductsimage>()
                        val variantList= arrayListOf<DataVariantMatrix>()
                    if(productListings.getJSONObject(0).getJSONArray("variantValues").length()==0)
                    {
                        val editor = sharedPreferencesStatus!!.edit()
                        editor.putString("variantJSONArray", productListings.toString())
                        editor.apply()
                        variant_checkbox.isChecked=false
                        recycler.visibility=View.GONE
                        variantCardView.visibility=View.GONE
                        unchecked_variant.visibility=View.VISIBLE
                        imageCardView.visibility=View.VISIBLE
                        tax.setText(productListings.getJSONObject(0).getString("isTaxIncluded"))
                        thresholdQty.setText(productListings.getJSONObject(0).getString("thresholdQuantity"))
                        base.setText(productListings.getJSONObject(0).getString("purchasePrice"))
                        sku.setText(productListings.getJSONObject(0).getString("skuCode"))
                        mrp.setText(productListings.getJSONObject(0).getString("mrp"))
                        barcode.setText(productListings.getJSONObject(0).getString("barcode"))
                        price.setText(productListings.getJSONObject(0).getString("sellingPrice"))
                        qty.setText(productListings.getJSONObject(0).getString("inStockQuantity"))
                        item_weight.setText(productListings.getJSONObject(0).getString("itemWeight"))
                        packaging_weight.setText(productListings.getJSONObject(0).getString("packagingWeight"))
                        min.setText(productListings.getJSONObject(0).getString("minOrderQty"))
                        max.setText(productListings.getJSONObject(0).getString("maxOrderQty"))
                        variantsOptions= JSONArray("[]")
                    }
                    else {
                        imageCardView.visibility=View.GONE
                        unchecked_variant.visibility=View.GONE
                        variant_checkbox.isChecked=true
                        recycler.visibility=View.VISIBLE
                        variantCardView.visibility=View.VISIBLE
                        variantsOptions=productObject.getJSONArray("variantValues")
                        tax.setText(productListings.getJSONObject(0).getString("isTaxIncluded"))
                        thresholdQty.setText(productListings.getJSONObject(0).getString("thresholdQuantity"))
                        for (j in 0 until productListings.length()) {
                            //    var medias=productListings.getJSONObject(j).getJSONArray("medias")
                            val variants =
                                productListings.getJSONObject(j).getJSONArray("variantValues")
                            variantValues=variants
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
                                productListings.getJSONObject(j).getString("packagingWeight"),
                                productListings.getJSONObject(j).getString("inStockQuantity")
                            )
                            variantList.add(VariantModel)
                        }
                        //shimmerFrameLayout.stopShimmer()
                        // shimmerFrameLayout.setVisibility(View.GONE)
                        //productlisting_recycler.setVisibility(View.VISIBLE)
                        /* if(productListing.size<1)
                    {
                        errortext.setVisibility(View.VISIBLE)
                        productlisting_recycler.setVisibility(View.GONE)
                    }
                    else{
                        errortext.setVisibility(View.GONE)
                        productlisting_recycler.setVisibility(View.VISIBLE)
                    }*/
                        val variantValuesRequestsFinal=JSONArray()
                        val allVariantsData=JSONArray(sharedPreferencesStatus?.getString("allVariantsData",null))
                        for(i in 0 until variantsOptions.length())
                        {
                            for(j in 0 until variantsOptions.getJSONObject(i).getJSONArray("variantValue").length())
                            {
                                val Object=JSONObject()
                                val VariantRequest=JSONObject()
                                val variantValuesRequests=JSONArray()
                                VariantRequest.put("isFlag",1)
                                VariantRequest.put("name",variantsOptions.getJSONObject(i).getString("variant"))
                                for(t in 0 until allVariantsData.length())
                                {
                                    if(allVariantsData.getJSONObject(t).getString("name")==variantsOptions.getJSONObject(i).getString("variant"))
                                    {
                                        VariantRequest.put("id",allVariantsData.getJSONObject(t).getInt("id"))
                                        break
                                    }
                                }
                                VariantRequest.put("supplierId",null)
                                Object.put("name",variantsOptions.getJSONObject(i).getJSONArray("variantValue").getString(j))
                                for(k in 0 until variantsOptions.getJSONObject(i).getJSONArray("variantValue").length())
                                {
                                    val Object2=JSONObject()
                                    Object2.put("name",variantsOptions.getJSONObject(i).getJSONArray("variantValue").getString(k))
                                    variantValuesRequests.put(Object2)
                                }
                                VariantRequest.put("variantValuesRequests",variantValuesRequests)
                                Object.put("variantRequest",VariantRequest)
                                variantValuesRequestsFinal.put(Object)
                            }
                        }

                        for( i in 0 until productListings.length())
                        {
                            val variantValuesRequestsFinalFinal=JSONArray()
                            for(j in 0 until productListings.getJSONObject(i).getJSONArray("variantValues").length())
                            {
                                for(k in 0 until variantValuesRequestsFinal.length())
                                {
                                    if(productListings.getJSONObject(i).getJSONArray("variantValues").getString(j)==variantValuesRequestsFinal.getJSONObject(k).getString("name"))
                                    {
                                        variantValuesRequestsFinalFinal.put(variantValuesRequestsFinal.getJSONObject(k))
                                    }
                                }
                            }
                            productListings.getJSONObject(i).put("mediaMappingRequests",productListings.getJSONObject((i)).getJSONArray("medias"))
                            productListings.getJSONObject(i).put("variantValuesRequests",variantValuesRequestsFinalFinal)
                        }
                        Log.i("ProductModel", variantList.toString())
                        val editor = sharedPreferencesStatus!!.edit()
                        editor.putString("variantJSONArray", productListings.toString())
                        editor.apply()
                        if (variantList.size > 0) {
                            recycler.visibility = View.VISIBLE
                        }
                        val Adapter =
                            VariantEditAdapter(this, variantList, productListings,variantsOptions)
                        val mLayoutManager = LinearLayoutManager(this)
                        recycler.layoutManager = mLayoutManager
                        recycler.adapter = Adapter
                        recycler.setHasFixedSize(true)
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
    fun setUpVariantRecycler(jsonArray: JSONArray)
    {
        if(jsonArray.length()<1)
        {
            imageCardView.visibility=View.VISIBLE
            unchecked_variant.visibility=View.VISIBLE
            variant_checkbox.isChecked=false
            recycler.visibility=View.GONE
            variantCardView.visibility=View.GONE
        }
        else
        {
            imageCardView.visibility=View.GONE
            unchecked_variant.visibility=View.GONE
            variant_checkbox.isChecked=true
            recycler.visibility=View.VISIBLE
            variantCardView.visibility=View.VISIBLE
        }
        val sets= ArrayList<ArrayList<String>>()
        for(i in 0 until jsonArray.length()) {
            val list=ArrayList<String>()
            for (j in 0 until jsonArray.getJSONObject(i).getJSONArray("variantValue").length()) {
                list.add(jsonArray.getJSONObject(i).getJSONArray("variantValue").getString(j))
            }
            sets.add(list)
        }
        generate(sets)
    }
    fun generate(sets: ArrayList<ArrayList<String>>) {
        val variantList= arrayListOf<DataVariantMatrix>()
        val productListing=JSONArray()
        val list:ArrayList<List<String>> =arrayListOf()
        var solutions = 1
        run {
            var i = 0
            while (i < sets.size) {
                solutions *= sets[i].size
                i++
            }
        }
        for (i in 0 until solutions) {
            var j = 1
            val list1:ArrayList<String> =arrayListOf()
            for (set in sets) {
                print(set[i / j % set.size].toString() + " ")
                list1.add(set[i / j % set.size])
                j *= set.size
            }
            list.add(list1)
            println()
        }
        Log.i("matrix",list.toString())
        Log.i("LIst",list.toString())
        if(list.size==1)
        {
            if(list[0].size!=0)
            {
                recycler.visibility= View.VISIBLE
            }
        }
        else if(list.size>1)
        {
            recycler.visibility=View.VISIBLE
        }
        for(i in 0 until list.size)
        {
            productListing.put(JSONObject("{}"))
            val data = list[i]
            var s = ""
            val variantValues=JSONArray()
            for (j in 0 until data.size) {
                s += data[j] + ","
                variantValues.put(data[j])
            }
            s=s.dropLast(1)
            val Model=DataVariantMatrix(s,"","","","","","","","","","")
            variantList.add(Model)
        }
        val editor = sharedPreferencesStatus!!.edit()
        editor.putString("variantJSONArray",productListing.toString())
        editor.apply()
        var Adapter =
            VariantEditAdapter(this, variantList,productListing,variantsOptions)
        val mLayoutManager = LinearLayoutManager(this)
        recycler.layoutManager = mLayoutManager
        recycler.adapter = Adapter
        recycler.setHasFixedSize(true)

    }

    override fun onBackPressed() {
        val editor = sharedPreferencesStatus!!.edit()
        editor.putString("variantJSONArray",null)
        editor.putString("sharedVariantOptions",null)
        editor.putString("ProductName",null)
        editor.putString("ProductCategory",null)
        editor.putString("ProductDescription",null)
        editor.putString("ProductID",null)
        editor.apply()
        finish()
    }

}


