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
import android.view.View
import android.widget.*
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
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.krenai.vendor.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*

class AddCollection : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private lateinit var auth: FirebaseAuth
    private  var firebaseUser: FirebaseUser?=null
    lateinit var clear:Button
    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""
    lateinit var title:EditText
    lateinit var description:EditText
    lateinit var submit:Button
    lateinit var btn_upload_image:ImageView
    lateinit var pb: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_collection)
        sharedPreferencesStatus = getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")
        userid=userid?.toDouble()?.toInt().toString()
        clear=findViewById<Button>(R.id.clear)
        title=findViewById(R.id.title)
        description=findViewById(R.id.description)
        btn_upload_image=findViewById(R.id.btn_upload_image)
        submit=findViewById(R.id.submit)
        pb=findViewById(R.id.progressbar)
        btn_upload_image.setOnClickListener {
            launchGallery()
        }
        clear.setOnClickListener {
            finish()
        }
        auth = Firebase.auth
        val user: FirebaseUser? = auth.currentUser
        submit.setOnClickListener {
            submit.visibility= View.GONE
            pb.visibility= View.VISIBLE
            if (user != null) {
                uploadImage()
            } else {
                signInAnonymously()
                uploadImage()
            }
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
                      //  Toast.LENGTH_SHORT).show()
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
        if (currentUser== null) {
            signInAnonymously()
        }
        else
        {
            firebaseUser=currentUser
        }
    }
    private fun updateUI(user: FirebaseUser?) {
        firebaseUser = user
    }
    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }
    private fun uploadImage(){
        if(filePath != null){
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val ref = storageRef.child("uploads/" + UUID.randomUUID().toString())
            Log.i("ref",ref.toString())
            Log.i("storageref",storageRef.toString())
            val uploadTask = ref?.putFile(filePath!!)
            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    addUploadRecordToDb(downloadUri.toString())
                    Log.i("downloaduri",downloadUri.toString())
                    addCollection(downloadUri.toString())
                } else {
                    // Handle failures
                    Toast.makeText(this, "Error! Please try again", Toast.LENGTH_SHORT).show()
                    submit.visibility=View.VISIBLE
                    pb.visibility=View.GONE
                    Log.i("downloaduri","FAILU")
                }
            }?.addOnFailureListener{
                Log.i("downloaduri","FAILUre")
                Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
                submit.visibility=View.VISIBLE
                pb.visibility=View.GONE
            }
            Log.i("urlTask",urlTask.toString())
        }else{
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
            submit.visibility=View.VISIBLE
            pb.visibility=View.GONE
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                btn_upload_image.setImageBitmap(bitmap)
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
              //  Toast.makeText(this, "Saved to DB", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                //Toast.makeText(this, "Error saving to DB", Toast.LENGTH_LONG).show()
            }
    }
    fun addCollection(uri:String)
    {
        val jsonParams= JSONObject()
        jsonParams.put("description", description.text.toString())
        jsonParams.put(  "id", 0)
        jsonParams.put("imageUrl",uri)
        jsonParams.put("name",title.text.toString())
        jsonParams.put("supplierId", userid)

        Log.i("jsonobject",jsonParams.toString())
        var url="https://www.outsourcecto.com/api/v3/store/collection/"
        val queue = Volley.newRequestQueue(this@AddCollection)
        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST,url, jsonParams,
                Response.Listener {
                    submit.visibility=View.VISIBLE
                    pb.visibility=View.GONE
                    try {

                        Log.i("RESPONSEcategory",it.toString())
                        Toast.makeText(this@AddCollection, it.getString("message"), Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {

                        Toast.makeText(
                            this@AddCollection,
                            "Incorrect Response!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
//                Toast.makeText(this@Order_Details, it.message, Toast.LENGTH_SHORT).show()

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
