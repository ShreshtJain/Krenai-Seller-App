package com.krenai.vendor.Activity.Authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.krenai.vendor.R
import com.krenai.vendor.utils.jkeys.Keys.CommonResources.CONNECTION_URL
import com.krenai.vendor.utils.jkeys.Keys.LoginKeys.DATA_PATH_OTP_VERIFY
import java.util.HashMap

class OTP : AppCompatActivity() {
    lateinit var to: ImageView
    lateinit var to2: TextView
    lateinit var create_button: Button
    private var linearLayoutLoading: LinearLayout? = null
    private var imageViewArrow: ImageView? = null
    var reg_mail: EditText?=null
    var new_pass:EditText?=null
    var otp:EditText?=null
    var response:String="false"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        linearLayoutLoading = findViewById(R.id.login_progress_verify_otp)
        imageViewArrow = findViewById(R.id.arrow_verify_otp)
        to=findViewById(R.id.fp)
        to2=findViewById(R.id.fp2)
        new_pass=findViewById(R.id.new_pass)
        otp=findViewById(R.id.otp)
        reg_mail=findViewById(R.id.reg_mail)
        create_button=findViewById(R.id.create_button)
        to.setOnClickListener {
            finish()
        }
        to2.setOnClickListener {
            finish()
        }
        create_button.setOnClickListener {
            imageViewArrow!!.visibility = View.GONE
            linearLayoutLoading!!.visibility = View.VISIBLE
            verify()
        }
    }
    fun verify() {
        val sMail = reg_mail!!.text.toString().trim { it <= ' ' }
        val sPass =new_pass!!.text.toString().trim { it <= ' ' }
        val sOtp = otp!!.text.toString().trim { it <= ' ' }
        val url: String = CONNECTION_URL+DATA_PATH_OTP_VERIFY+sMail+"/"+sPass+"/"+sOtp
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val jsonObjectRequest: StringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { jsonObject ->
                imageViewArrow!!.visibility = View.VISIBLE
                linearLayoutLoading!!.visibility = View.GONE
                response=jsonObject.toString()
                if(response=="true")
                {
                    Toast.makeText(this, "Password Changed Successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Login::class.java))
                    finishAffinity()
                    finish()
                }
                else
                {
                    Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { volleyError ->
                imageViewArrow!!.visibility = View.VISIBLE
                linearLayoutLoading!!.visibility = View.GONE
                if(sMail=="" || sPass=="" || sOtp=="")
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
