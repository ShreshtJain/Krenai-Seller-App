package com.krenai.vendor.Activity.Authentication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.krenai.vendor.Activity.MainActivity
import com.krenai.vendor.R
import com.krenai.vendor.utils.context.ApplicationContext

import com.krenai.vendor.utils.jkeys.Keys.CommonResources.CONNECTION_URL
import com.krenai.vendor.utils.jkeys.Keys.FireBaseToken.DATA_PATH_FIREBASE_UPDATE_DEVICE_TOKEN
import com.krenai.vendor.utils.jkeys.Keys.FireBaseToken.DATA_PATH_REFRESH_FIREBASE_TOKEN
import com.krenai.vendor.utils.jkeys.Keys.LoginKeys.*
import com.krenai.vendor.utils.network.VolleySingleton
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

class Login : AppCompatActivity(), View.OnClickListener {
    private var buttonLogin: Button? = null
    private var linearLayoutLoading: LinearLayout? = null
    private var imageViewArrow: ImageView? = null
    private val returnStatus = 0
    private var onRecentBackPressedTime: Long = 0
    private var sMobile: String? = null
    private var sPassword: String? = null
    private var textViewForgotPassword: TextView? = null
    private var editTextPhone: EditText? = null
    private var editTextPassword: EditText? = null
    private var sharedPreferencesStatus: SharedPreferences? = null
    private var awesomeValidation: AwesomeValidation? = null
    private var userid:String? = ""
    private var token:String? = ""
    private  var FCM_Token:String?=""

    val charset: Charset = Charsets.UTF_8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // mAuth = FirebaseAuth.getInstance()
           ApplicationContext.getsInstance()
        sharedPreferencesStatus = getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        updateScreenResolution();
        referenceId
    }

    private val referenceId: Unit
        private get() {
            awesomeValidation = AwesomeValidation(ValidationStyle.COLORATION)
            buttonLogin = findViewById(R.id.sign_in_button)
            linearLayoutLoading = findViewById(R.id.login_progress)
            textViewForgotPassword = findViewById(R.id.forgot_password_text)
            textViewForgotPassword?.setOnClickListener(this)
            imageViewArrow = findViewById(R.id.arrow)
            buttonLogin?.setOnClickListener(this)
            editTextPhone = findViewById(R.id.reg_phone)
            editTextPassword = findViewById(R.id.reg_safe_code)
        }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - onRecentBackPressedTime > 2000) {
            onRecentBackPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show()
            return
        }
        finishAffinity()
        finish()
    }
    override fun onClick(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.forgot_password_text -> {
                intent=Intent(this, ForgetPassword::class.java)
                startActivity(intent)
            }
 R.id.sign_in_button ->
     if (awesomeValidation!!.validate()) {
       imageViewArrow!!.visibility = View.GONE
       linearLayoutLoading!!.visibility = View.VISIBLE
       Handler().postDelayed({
           if (returnStatus == 0) {
               login()
           }
       }, SPLASH_TIME_OUT.toLong())}

}
}

private fun login() {
sMobile = editTextPhone!!.text.toString().trim { it <= ' ' }
sPassword = editTextPassword!!.text.toString().trim { it <= ' ' }
val url: String = CONNECTION_URL+ DATA_PATH_LOGIN
val requestQueue: RequestQueue = Volley.newRequestQueue(this@Login)

val jsonObjectRequest: StringRequest = object : StringRequest(
   Method.POST, url,
   Response.Listener { jsonObject -> parseLoginJSONObject(jsonObject) },
   Response.ErrorListener { volleyError ->
       imageViewArrow!!.visibility = View.VISIBLE
       linearLayoutLoading!!.visibility = View.GONE
       if(volleyError.toString()=="com.android.volley.AuthFailureError") {
           imageViewArrow!!.visibility = View.VISIBLE
           linearLayoutLoading!!.visibility = View.GONE
           Toast.makeText(this@Login, "Invalid Credentials", Toast.LENGTH_SHORT).show()
       }
       else if(volleyError.toString()=="com.android.volley.NoConnectionError: java.net.UnknownHostException: Unable to resolve host \"www.krenai.online\": No address associated with hostname")
       {
           imageViewArrow!!.visibility = View.VISIBLE
           linearLayoutLoading!!.visibility = View.GONE
           Toast.makeText(this@Login, "Network Error", Toast.LENGTH_SHORT).show()
       }
       else
       {
           imageViewArrow!!.visibility = View.VISIBLE
           linearLayoutLoading!!.visibility = View.GONE
           Toast.makeText(this@Login, "Some Error has Occurred", Toast.LENGTH_SHORT).show()
       }
   }
) {
   @Throws(AuthFailureError::class)
   override fun getHeaders(): Map<String, String> {
       val headers =
           HashMap<String, String>()
       headers["Content-Type"] = "application/x-www-form-urlencoded"
       headers["Authorization"] = "Basic VVNFUl9TVVBQTElFUl9BUFA6cGFzc3dvcmQ="

       return headers
   }

   override fun getBodyContentType(): String {
       return "application/x-www-form-urlencoded; charset=UTF-8"
   }

   override fun getParams(): Map<String, String> {
       val params: MutableMap<String, String> =
           HashMap()
       params["username"] = sMobile.toString()!!
       params["password"] = sPassword.toString()!!
       params["grant_type"] ="password"!!
       return params
   }

   override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
       return try {
           val json = String(
               response.data, charset
           )
           if (json.length == 0) {
               Response.success(
                   null,
                   HttpHeaderParser.parseCacheHeaders(response)
               )
           } else {
               super.parseNetworkResponse(response)
           }
       } catch (e: UnsupportedEncodingException) {
           Response.error(
               ParseError(e)
           )
       }
   }
}
jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
   50000,
   DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
   DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
)
requestQueue.add(jsonObjectRequest)
}

private fun parseLoginJSONObject(jsonObject: String) {
    imageViewArrow!!.visibility = View.VISIBLE
    linearLayoutLoading!!.visibility = View.GONE
val map: Map<*, *> = Gson().fromJson(
   jsonObject,
   HashMap::class.java
)
    val editor = sharedPreferencesStatus!!.edit()
    Log.d(TAG, "" + jsonObject)
    Log.i("TOKEN",jsonObject)
    editor.putString("TOKEN", map[TOKEN].toString())
    editor.putString("Refresh_Token",map[Refresh_Token].toString())
    editor.putString("USER_ID", map[USER_ID].toString())
    editor.putInt("STATUS", 4)
    editor.apply()
    userid=sharedPreferencesStatus?.getString("USER_ID","")
    token=sharedPreferencesStatus?.getString("TOKEN","")
    Toast.makeText(this@Login, "Successfully Logged In", Toast.LENGTH_SHORT).show()
    initFCM()
    startActivity(
        Intent(
            this@Login,
            MainActivity::class.java
        ))
}

private fun updateScreenResolution() {
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
   val w = window
   w.addFlags(
       WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

   )
}
}

private fun initFCM(){
    FirebaseInstanceId.getInstance().instanceId
        .addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            // Get new Instance ID token
            val fcmToken = task.result?.token
            FCM_Token=fcmToken
           sendToken(FCM_Token)
        })
}
    fun sendToken(s: String?) {
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")
        userid=userid?.toDouble()?.toInt().toString()
        val fcm=sharedPreferencesStatus?.getString("NEW_TOKEN","")
        val url= CONNECTION_URL+ DATA_PATH_FIREBASE_UPDATE_DEVICE_TOKEN+userid?.toDouble()?.toInt().toString()+"/"+s.toString()
        val queue = Volley.newRequestQueue( this)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url,
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {

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

companion object {
private val TAG = Login::class.java.simpleName
private const val SPLASH_TIME_OUT = 500
private const val loginStatus = "loginStatus"
}
}