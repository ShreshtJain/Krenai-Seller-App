package com.krenai.vendor.Activity.Authentication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.inappmessaging.MessagesProto
import com.krenai.vendor.R
import com.krenai.vendor.utils.jkeys.Keys.CommonResources.CONNECTION_URL
import com.krenai.vendor.utils.jkeys.Keys.ForgotPassword.DATA_PATH_VERIFY_PHONE
import java.io.UnsupportedEncodingException
import java.util.HashMap

class ForgetPassword : AppCompatActivity() {
    private var linearLayoutLoading: LinearLayout? = null
    private var imageViewArrow: ImageView? = null
    lateinit var verify_button:Button
    lateinit var toLogin:ImageView
    lateinit var toLogin2:TextView
    private var sMobile: String? = null
    var response:String="false"
    var reg_mail:EditText?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        linearLayoutLoading = findViewById(R.id.login_progress_verify)
        imageViewArrow = findViewById(R.id.arrow_verify)
        reg_mail=findViewById(R.id.reg_mail)
        verify_button=findViewById(R.id.verify_button)
        toLogin=findViewById(R.id.toLogin)
        toLogin2=findViewById(R.id.toLogin2)
        updateScreenResolution()
        toLogin.setOnClickListener {
            finish()
        }
        toLogin2.setOnClickListener {
            finish()
        }
        verify_button.setOnClickListener {
            imageViewArrow!!.visibility = View.GONE
            linearLayoutLoading!!.visibility = View.VISIBLE
            verify()
        }
    }

    private fun updateScreenResolution() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window
            w.addFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

            )
        }
    }
    fun verify() {
        sMobile = reg_mail!!.text.toString().trim { it <= ' ' }
        val url: String = CONNECTION_URL+DATA_PATH_VERIFY_PHONE +sMobile
        val requestQueue: RequestQueue = Volley.newRequestQueue(this@ForgetPassword)
        val jsonObjectRequest: StringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { jsonObject ->
                response=jsonObject.toString()
                imageViewArrow!!.visibility = View.VISIBLE
                linearLayoutLoading!!.visibility = View.GONE
                if(response=="true")
                {
                    Toast.makeText(this, "OTP sent successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, OTP::class.java))
                }
                else
                {
                    Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { volleyError ->
                imageViewArrow!!.visibility = View.VISIBLE
                linearLayoutLoading!!.visibility = View.GONE
                if(sMobile=="")
                {
                    Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show()
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
        }
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            50000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(jsonObjectRequest)
    }
}
