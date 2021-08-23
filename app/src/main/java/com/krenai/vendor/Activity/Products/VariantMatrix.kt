package com.krenai.vendor.Activity.Products

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
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
import com.krenai.vendor.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*

class VariantMatrix : AppCompatActivity() {
    lateinit var progressLinearLayout: LinearLayout
    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""
    lateinit var clear: Button
    private val PICK_IMAGE_REQUEST = 71
    private var firebaseuri:String?=null
    private lateinit var auth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null
    private lateinit var save:TextView
    private var filePath: Uri? = null
    lateinit var update_image:ImageView
    lateinit var name:TextView
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
    var image=0
    var variantValuesRequests=JSONArray()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_variant_matrix)
        sharedPreferencesStatus = getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid = sharedPreferencesStatus?.getString("USER_ID", "")
        token = sharedPreferencesStatus?.getString("TOKEN", "")
        userid = userid?.toDouble()?.toInt().toString()
        update_image=findViewById(R.id.update_image)
        clear = findViewById(R.id.clear)
        save = findViewById(R.id.save)
         name =findViewById(R.id.text)
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
         progressLinearLayout=findViewById(R.id.progress_linear_layout)
        variantValuesRequests=JSONArray(getIntent().getStringExtra("variantValuesRequests"))
        name.setText(getIntent().getStringExtra("name").toString())
        base.setText(getIntent().getStringExtra("base").toString())
        sku.setText(getIntent().getStringExtra("sku").toString())
        mrp.setText(getIntent().getStringExtra("mrp").toString())
        barcode.setText(getIntent().getStringExtra("barcode").toString())
        price.setText(getIntent().getStringExtra("price").toString())
        qty.setText(getIntent().getStringExtra("qty").toString())
        item_weight.setText(getIntent().getStringExtra("iWeight").toString())
        packaging_weight.setText(getIntent().getStringExtra("pWeight").toString())
        min.setText(getIntent().getStringExtra("min").toString())
        max.setText(getIntent().getStringExtra("max").toString())
        save.setOnClickListener {
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
            else {
                progressLinearLayout.visibility=View.VISIBLE
                val Object = JSONObject()
                val MediaObject = JSONObject()
                MediaObject.put("mediaName", "img01")
                MediaObject.put("mediaUrl", "")
                MediaObject.put("supplierId", userid)
                if (filePath != null) {
                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.reference
                    val ref = storageRef.child("uploads/" + UUID.randomUUID().toString())
                    Log.i("ref", ref.toString())
                    Log.i("storageref", storageRef.toString())
                    val uploadTask = ref?.putFile(filePath!!)
                    val urlTask =
                        uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                            if (!task.isSuccessful) {
                                progressLinearLayout.visibility=View.GONE
                                task.exception?.let {
                                    throw it
                                }
                            }
                            return@Continuation ref.downloadUrl
                        })?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                progressLinearLayout.visibility=View.GONE
                                val downloadUri = task.result
                                firebaseuri = downloadUri.toString()
                                addUploadRecordToDb(downloadUri.toString())
                                Log.i("downloaduri", downloadUri.toString())
                                val jsonParams = JSONObject()
                                val jsonArray = JSONArray()
                                val jsonmedia = JSONObject()
                                jsonmedia.put("mediaName", "string")
                                jsonmedia.put("mediaUrl", firebaseuri)
                                MediaObject.put("mediaUrl", firebaseuri)
                                jsonmedia.put("supplierId", userid)
                                jsonmedia.put("tags", 0)
                                jsonArray.put(jsonmedia)
                                jsonParams.put("medias", jsonArray)
                                MediaObject.put("tags", 0)
                                Log.i("jsonobject", jsonParams.toString())
                                var url = "https://www.outsourcecto.com/api/v3/stores/medias/bulk"
                                val queue = Volley.newRequestQueue(this)
                                val jsonObjectRequest =
                                    object : JsonObjectRequest(Method.POST, url, jsonParams,
                                        Response.Listener {
                                            Log.i("RESPONSEcategoryr", it.toString())
                                            try {
                                                Log.i("RESPONSEcategory", it.toString())
                                                // Toast.makeText(this, it.getString("message"), Toast.LENGTH_SHORT).show()
                                                var mediaId =
                                                    it.getJSONArray("object").getJSONObject(0)
                                                        .getInt("id")
                                                jsonParams.getJSONArray("medias").getJSONObject(0)
                                                    .put("id", mediaId)
                                                Log.i("mediaId", mediaId.toString())
                                                jsonmedia.put("id", mediaId)
                                                MediaObject.put("id", mediaId)
                                                val mediaArr = JSONArray()
                                                mediaArr.put(MediaObject)
                                                jsonArray.put(MediaObject)
                                                Object.put("skuCode", sku.text.toString())
                                                Object.put("sellingPrice", price.text.toString())
                                                Object.put("mrp", mrp.text.toString())
                                                Object.put("purchasePrice", base.text.toString())
                                                Object.put("inStockQuantity", qty.text.toString())
                                                val arr = JSONArray()
                                                val variantValuesRequestsFinal = JSONArray()
                                                val list = name.text.toString().split(",")
                                                    .map { it.trim() }
                                                for (i in 0 until list.size) {
                                                    arr.put(i, list[i])
                                                    for (j in 0 until variantValuesRequests.length()) {
                                                        if (list[i] == variantValuesRequests.getJSONObject(
                                                                j
                                                            ).getString("name")
                                                        ) {
                                                            variantValuesRequestsFinal.put(
                                                                variantValuesRequests.getJSONObject(
                                                                    j
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                                Object.put("variantValues", arr)
                                                Object.put("barcode", barcode.text.toString())
                                                Object.put("minOrderQty", min.text.toString())
                                                Object.put("maxOrderQty", max.text.toString())
                                                Object.put(
                                                    "itemWeight",
                                                    item_weight.text.toString()
                                                )
                                                Object.put(
                                                    "packagingWeight",
                                                    packaging_weight.text.toString()
                                                )
                                                Object.put("medias", mediaArr)
                                                Object.put("mediaMappingRequests", mediaArr)
                                                Object.put(
                                                    "variantValuesRequests",
                                                    variantValuesRequestsFinal
                                                )
                                                val s = sharedPreferencesStatus?.getString(
                                                    "variantJSONArray",
                                                    null
                                                )
                                                val productListing = JSONArray(s)
                                                productListing.put(
                                                    getIntent().getIntExtra(
                                                        "position",
                                                        0
                                                    ), Object
                                                )

                                                if (sku.text.length > 0 && qty.text.length > 0 && base.text.length > 0 && price.text.length > 0 && mrp.text.length > 0) {
                                                    val editor = sharedPreferencesStatus!!.edit()
                                                    editor.putString(
                                                        "variantJSONArray",
                                                        productListing.toString()
                                                    )
                                                    editor.apply()
                                                    Log.i("variantJSON", Object.toString())
                                                    Log.i(
                                                        "variantJSONarray",
                                                        productListing.toString()
                                                    )

                                                    finish()
                                                }
                                            } catch (e: Exception) {
                                                progressLinearLayout.visibility=View.GONE
                                                Toast.makeText(
                                                    this,
                                                    "Incorrect Media Response!!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }, Response.ErrorListener {
                                            Toast.makeText(this, it.message, Toast.LENGTH_SHORT)
                                                .show()
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
                            } else {
                                progressLinearLayout.visibility=View.GONE
                                Toast.makeText(this, "Error! Please try again", Toast.LENGTH_SHORT)
                                    .show()
                                Log.i("downloaduri", "FAILU")
                            }
                        }?.addOnFailureListener {
                            Log.i("downloaduri", "FAILUre")
                            Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
                            progressLinearLayout.visibility=View.GONE
                        }
                    Log.i("urlTask", urlTask.toString())
                } else {
                    Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
                    progressLinearLayout.visibility=View.GONE
                }
            }
        }
        clear.setOnClickListener {
            finish()
        }
        auth = Firebase.auth
        val user: FirebaseUser? = auth.currentUser
        update_image.setOnClickListener {
            if(user!=null)
            {
                launchGallery()
            }
            else
            {
                signInAnonymously()
                launchGallery()
            }
        }
    }
    private fun signInAnonymously() {
        // [START signin_anonymously]
        auth.signInAnonymously()
            .addOnCompleteListener( ) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
               // Log.i("signedin", "signInAnonymously:success")
                val user = auth.currentUser
                updateUI(user)
            } else {
                // If sign in fails, display a message to the user.
                //Log.i("signedinfail", "signInAnonymously:failure", task.exception)
                updateUI(null)
            }

            // [START_EXCLUDE]
            // [END_EXCLUDE]
        }
        // [END signin_anonymously]
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser == null) {
            signInAnonymously()
        } else {
            firebaseUser = currentUser
        }
    }
    private fun updateUI(user: FirebaseUser?) {
        firebaseUser = user
    }
     fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }

            filePath = data.data

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
               update_image.setImageBitmap(bitmap)
                //uploadImage()

            } catch (e: IOException) {
                e.printStackTrace()
            }
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
              //  Toast.makeText(this, "Error saving to DB", Toast.LENGTH_LONG).show()
            }
    }

}
