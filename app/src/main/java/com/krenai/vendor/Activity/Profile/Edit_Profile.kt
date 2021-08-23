package com.krenai.vendor.Activity.Profile

import android.app.Activity
import android.content.Intent
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
import com.krenai.vendor.Complete
import com.krenai.vendor.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*

class Edit_Profile : AppCompatActivity() {


    lateinit var edittextFirstName:EditText
    lateinit var edittextlastName:EditText
    lateinit var edittextUsername:EditText
    lateinit var edittextBusiness:EditText
    lateinit var edittextAddressLine1: EditText
    lateinit var edittextAddressLine2: EditText
    lateinit var edittextCity: EditText
    lateinit var edittextState: EditText
    lateinit var edittextCountry: EditText
    lateinit var edittextPincode: EditText
    lateinit var edittextPhone:EditText
    lateinit var edittextEmail:EditText
    lateinit var clear: Button
    lateinit var save: TextView
    lateinit var userImage:CircleImageView
    lateinit var pb:ProgressBar
    lateinit var uploadImage:ImageView

    private  var profilePicUrl:String?=null

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseuri:String?=null
    private  var firebaseUser: FirebaseUser?=null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        clear=findViewById(R.id.clear)
        save=findViewById(R.id.save)
        pb=findViewById(R.id.pb)
        uploadImage=findViewById(R.id.edit_profile_pic)

        edittextFirstName=findViewById(R.id.edittext_first_name)
        edittextlastName=findViewById(R.id.edittext_last_name)
        edittextUsername=findViewById(R.id.edittext_username)
        edittextEmail=findViewById(R.id.edittext_email)
        edittextPhone=findViewById(R.id.edittext_phone)
        edittextBusiness=findViewById(R.id.edittext_business)

        edittextAddressLine1=findViewById(R.id.edittext_address_line_1)
        edittextAddressLine2=findViewById(R.id.edittext_address_line_2)
        edittextCity=findViewById(R.id.edittext_city)
        edittextCountry=findViewById(R.id.edittext_country)
        edittextState=findViewById(R.id.edittext_state)
        edittextPincode=findViewById(R.id.edittext_pincode)

        userImage=findViewById(R.id.profile_image_user)

        if(getIntent().getStringExtra("userName")!=null) {
            edittextUsername.setText(getIntent().getStringExtra("userName"))
        }
        if(getIntent().getStringExtra("firstName")!=null) {
            edittextFirstName.setText(getIntent().getStringExtra("firstName"))
        }
        if(getIntent().getStringExtra("lastName")!=null) {
            edittextlastName.setText(getIntent().getStringExtra("lastName"))
        }
        if(getIntent().getStringExtra("mailId")!=null) {
            edittextEmail.setText(getIntent().getStringExtra("mailId"))
        }
        if(getIntent().getStringExtra("phoneNumber")!=null) {
            edittextPhone.setText(getIntent().getStringExtra("phoneNumber")?.subSequence(4,14))
        }
        if(getIntent().getStringExtra("legalNameOfBusiness")!=null) {
            edittextBusiness.setText(getIntent().getStringExtra("legalNameOfBusiness"))
        }
        if(getIntent().getStringExtra("addressLine1")!=null) {
            edittextAddressLine1.setText(getIntent().getStringExtra("addressLine1"))
        }
        if(getIntent().getStringExtra("addressLine2")!=null) {
            edittextAddressLine2.setText(getIntent().getStringExtra("addressLine2"))
        }
        if(getIntent().getStringExtra("city")!=null) {
            edittextCity.setText(getIntent().getStringExtra("city"))
        }
        if(getIntent().getStringExtra("addressState")!=null) {
            edittextState.setText(getIntent().getStringExtra("addressState"))
        }
        if(getIntent().getStringExtra("country")!=null) {
            edittextCountry.setText(getIntent().getStringExtra("country"))
        }
        if(getIntent().getStringExtra("pincode")!=null) {
            edittextPincode.setText(getIntent().getStringExtra("pincode"))
        }
        if(getIntent().getStringExtra("userImageUrl")!=null)
        {
            profilePicUrl=getIntent().getStringExtra("userImageUrl")
            Picasso.get().load(profilePicUrl).into(userImage)
        }

        auth = Firebase.auth
        val user: FirebaseUser? = auth.currentUser


        clear.setOnClickListener{
            finish()
        }
        save.setOnClickListener {
            if(edittextUsername.text.toString() == "")
            {
                edittextUsername.setError("Manadatory to fill this field")
            }
            if(edittextFirstName.text.toString() == "")
            {
                edittextFirstName.setError("Mandatory to fill this field")
            }
            if( edittextAddressLine1.text.toString() == "")
            {
                edittextAddressLine1.setError("Mandatory to fill this field")
            }
            if(edittextState.text.toString() == "")
            {
                edittextState.setError("Invalid State")
            }
            if(edittextCity.text.toString() == "")
            {
                edittextCity.setError("Invalid City")
            }
            if(edittextCountry.text.toString() == "")
            {
                edittextCountry.setError("Invalid Country")
            }
            if(edittextPincode.text.toString().length<6 || edittextPincode.text.toString().length>6)
            {
                edittextPincode.setError("Invalid Pincode")
            }
            if(!(edittextPhone.text.toString().matches(("^[6-9]\\d{9}\$").toRegex())))
            {
                edittextPhone.setError("Invalid Phone Number")
            }
            if(edittextBusiness.text.toString() == "")
            {
                edittextBusiness.setError("Invalid Business Name")
            }
            if(!(edittextEmail.text.toString().matches(("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$").toRegex())))
            {
                edittextEmail.setError("Invalid Email Id")
            }
            if(!((edittextUsername.text.toString() == "") || (!(edittextEmail.text.toString().matches(("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$").toRegex())))
                        || (edittextBusiness.text.toString() == "") || (!(edittextPhone.text.toString().matches(("^[6-9]\\d{9}\$").toRegex())))
                        || (edittextPincode.text.toString().length<6 || edittextPincode.text.toString().length>6)
                        || (edittextCountry.text.toString() == "") || (edittextCity.text.toString() == "") || (edittextState.text.toString() == "")
                        || (edittextFirstName.text.toString() == "") || ( edittextAddressLine1.text.toString() == ""))) {
                if (user != null) {
                    save.visibility= View.GONE
                    pb.visibility= View.VISIBLE
                    uploadImage()
                } else {
                    save.visibility= View.GONE
                    pb.visibility= View.VISIBLE
                    signInAnonymously()
                    uploadImage()
                }
            }
        }

        uploadImage.setOnClickListener {
            launchGallery()
        }
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
                userImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun uploadImage(){
        if(filePath != null)
        {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val ref = storageRef.child("uploads/" + UUID.randomUUID().toString())
            val uploadTask = ref.putFile(filePath!!)

            val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    firebaseuri=downloadUri.toString()
                    addUploadRecordToDb(downloadUri.toString())
                    Log.i("downloaduri",downloadUri.toString())

                    profilePicUrl=firebaseuri
                    editProfile()
                }
            }.addOnFailureListener{
            }
        }
        else{
                editProfile()
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
    fun editProfile()
    {
        val userId=getIntent().getStringExtra("userId")
        val url="https://www.krenai.online/api/v3/account/suppliers/"+userId
        val storeId=getIntent().getStringExtra("storeId")
        val token=getIntent().getStringExtra("token")

        val jsonParams= JSONObject("{\n" +
                "  \"storeRequests\": [\n" +
                "    {\n" +
                "      \"id\":${storeId?.toInt()},\n" +
                "      \"addressLine1\": \"${edittextAddressLine1.text}\",\n" +
                "      \"addressLine2\": \"${edittextAddressLine2.text}\",\n" +
                "      \"addressState\": \"${edittextState.text}\",\n" +
                "      \"city\": \"${edittextCity.text}\",\n" +
                "      \"country\": \"${edittextCountry.text}\",\n" +
                "      \"legalNameOfBusiness\":\"${edittextBusiness.text}\",\n" +
                "      \"pincode\": \"${edittextPincode.text}\",\n" +
                "      \"supplierId\": 0\n" +
                "    }\n" +
                "  ],\n" +
                "  \"firstName\":\" ${edittextFirstName.text}\",\n" +
                "  \"lastName\": \"${edittextlastName.text}\",\n" +
                "  \"userName\":\"${edittextUsername.text}\",\n" +
                "  \"emailId\": \"${edittextEmail.text}\",\n" +
                "  \"phoneNo\": \"${edittextPhone.text}\",\n" +
                "  \"profilePicUrl\": \"${profilePicUrl.toString()}\"\n" +
                "}")
        Log.i("JSONparams",jsonParams.toString())

        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest =
            object : JsonObjectRequest(Method.PUT,url, jsonParams,
                Response.Listener {
                    save.visibility= View.VISIBLE
                    pb.visibility= View.GONE

                    try {

                        Log.i("RESPONSE",it.toString())

                        if(it.getString("message").equals("Successfully Updated"))
                            startActivity(Intent(this, Complete::class.java))
                        else
                            Toast.makeText(this, it.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                    catch (e: Exception) {
                        Toast.makeText(
                            this,
                            "Incorrect Response!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    save.visibility= View.VISIBLE
                    pb.visibility= View.GONE
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
    }
}
