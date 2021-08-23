package com.krenai.vendor.Activity.Authentication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.gson.Gson
import com.krenai.vendor.Activity.MainActivity

import com.krenai.vendor.R
import com.krenai.vendor.utils.Firebase.MyFirebaseMessagingService
import com.krenai.vendor.utils.jkeys.Keys
import com.krenai.vendor.utils.jkeys.Keys.CommonResources.CONNECTION_URL
import com.krenai.vendor.utils.jkeys.Keys.LoginKeys.DATA_PATH_LOGIN
import com.krenai.vendor.utils.jkeys.Keys.LoginKeys.STATUS
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.HashMap


class SplashActivity : AppCompatActivity() {
   private  var FCM_Token:String?=""
    private var sharedPreferenceStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""
    private var returnStatus:Int? = 0
    private var refresh_token:String?=""
    private var alertMessage: LinearLayout? = null
    private var mLocationManager: LocationManager? = null
    private var isGPSEnabled = false
    private var isNetworkEnabled = false
    val charset: Charset = Charsets.UTF_8
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        updateScreenResolution()
        sharedPreferenceStatus = getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        returnStatus = sharedPreferenceStatus?.getInt("STATUS", 0)
        refresh_token=sharedPreferenceStatus?.getString("Refresh_Token","")
        userid=sharedPreferenceStatus?.getString("USER_ID","")
        token=sharedPreferenceStatus?.getString("TOKEN","")
        FCM_Token=sharedPreferenceStatus?.getString("FCM","")
        CheckLocationStatus()
    }


    private fun CheckLocationStatus() {
        Handler().postDelayed({
            Log.d("RETURN STATUS", ":: $returnStatus")
            if (returnStatus == 0 || returnStatus == 2 || returnStatus == 1) {
                val i = Intent(this@SplashActivity, IntroSliderActivity::class.java)
                startActivity(i)
                finish()
            } else if (returnStatus == 4) {
                if (isOnline) {
                    if (isLocationServicesEnabled) {
                        getAccsessToken()
                    } else {
                        showSettingsAlert()
                    }
                } else {
                    alertMessage =
                        findViewById<View>(R.id.alert_message) as LinearLayout
                    alertMessage!!.visibility = View.VISIBLE
                    checkForConnectivity()
                }
            }
        }, 0)
    }

    private fun checkForConnectivity() {
        Handler().postDelayed({
            Log.d("connection", "Checking Connection")
            if (isOnline) {
                if (isLocationServicesEnabled) {
                    getAccsessToken()
                }
            } else {
                checkForConnectivity()
            }
        }, 0)
    }

    val isLocationServicesEnabled: Boolean
        get() {
            try {
                mLocationManager =
                    getSystemService(Context.LOCATION_SERVICE) as LocationManager
                isGPSEnabled = mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                isNetworkEnabled =
                    mLocationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                return if (!isGPSEnabled && !isNetworkEnabled) {
                    false
                } else {
                    true
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                        this@SplashActivity,
                        "Unable to connect to server",
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
            return false
        }

    protected val isOnline: Boolean
        protected get() {
            val cm =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = cm.activeNetworkInfo
            return if (networkInfo != null && networkInfo.isConnected) {
                true
            } else {
                false
            }
        }

    fun showSettingsAlert() {
        if ( this@SplashActivity.isFinishing) {
            return
        }
        runOnUiThread {
            val alertDialog =
                AlertDialog.Builder(
                    this@SplashActivity
                )
            alertDialog.setTitle("GPS is settings")
            alertDialog
                .setMessage("Your GPS is disabled, Enable GPS in settings or continue with approximate location")
            alertDialog.setPositiveButton(
                "Settings"
            ) { dialog, which ->
                val intentRedirectionGPSSettings =
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                intentRedirectionGPSSettings.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                startActivityForResult(intentRedirectionGPSSettings, 0)
                val intent = Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                this@SplashActivity.startActivity(intent)
            }
            alertDialog.setNegativeButton(
                "Cancel"
            ) { dialog, which ->
                Toast.makeText(
                    this@SplashActivity,
                    "App need your location",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            alertDialog.create().show()
        }
    }

    private fun updateScreenResolution() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
    }
    fun getAccsessToken() {
        val url: String = CONNECTION_URL+ DATA_PATH_LOGIN
        val requestQueue: RequestQueue = Volley.newRequestQueue(this@SplashActivity)

        val jsonObjectRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { jsonObject -> parseLoginJSONObject(jsonObject) },
            Response.ErrorListener { volleyError ->
                if (volleyError.networkResponse.statusCode == 400) {
                    Toast.makeText(
                        this@SplashActivity,
                        "Account is not verified.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this@SplashActivity, "Network Error", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers =
                    HashMap<String, String>()
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                headers["Authorization"] = " Basic VVNFUl9TVVBQTElFUl9BUFA6cGFzc3dvcmQ="

                return headers
            }

            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded; charset=UTF-8"
            }

            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                    HashMap()
                params[Keys.LoginKeys.Refresh_Token] = refresh_token.toString()!!
                params["grant_type"] = "refresh_token"!!
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
        val map: Map<*, *> = Gson().fromJson(
            jsonObject,
            HashMap::class.java
        )
        val editor = sharedPreferenceStatus!!.edit()
        Log.i("splash object", "" + jsonObject)
        Log.i("TOKEN",jsonObject)
        editor.putString("TOKEN", map[Keys.LoginKeys.TOKEN].toString())
        editor.putString("Refresh_Token",map[Keys.LoginKeys.Refresh_Token].toString())
        editor.putString("USER_ID", map[Keys.LoginKeys.USER_ID].toString())
        editor.putInt("STATUS", 4)
        editor.putStringSet("stringset", null)
        editor.putStringSet("intset",null)
        editor.putStringSet("stringset2",null)
        editor.putStringSet("intset2",null)
        editor.putStringSet("brand_checkedlist",null)
        editor.putStringSet("category_checkedlist",null)
        editor.putStringSet("subcategory_checkedlist",null)
        editor.putStringSet("subsubcategory_checkedlist",null)
        editor.putString("sharedVariantkey",null)
        editor.putString("sharedVariantvalue",null)
        editor.putString("variantJSONArray",null)
        editor.putString("sharedVariantOptions",null)
        editor.putString("ProductName",null)
        editor.putString("ProductCategory",null)
        editor.putString("ProductDescription",null)
        editor.putString("ProductID",null)
        editor.apply()
        startActivity(
            Intent(
                this@SplashActivity,
                MainActivity::class.java
            ))
        finish()
    }


    companion object {
        private const val SPLASH_TIME_OUT = 2000
    }
}