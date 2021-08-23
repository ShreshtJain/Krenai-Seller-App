package com.krenai.vendor.Fragment

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.krenai.vendor.Activity.Authentication.Login
import com.krenai.vendor.Activity.Profile.Edit_Profile
import com.krenai.vendor.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap
/**
 * Created by Shresht on 21-09-2020.
 */
class Profile : Fragment() {

    lateinit var editProfileButton:ImageView
    lateinit var profileImageUser:CircleImageView
    lateinit var userName:TextView
    lateinit var legalNameOfBusiness:TextView
    lateinit var textName:TextView
    lateinit var mailId:TextView
    lateinit var textPhoneNumber:TextView
    lateinit var addressLine1:TextView
    lateinit var addressLine2:TextView
    lateinit var userCity:TextView
    lateinit var userState:TextView
    lateinit var userCountry:TextView
    lateinit var userPincode:TextView
    private var url="https://www.krenai.online/api/v3/account/suppliers/"
    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""
    private var profilePicUrl:String=""
    private var firstName:String=""
    private var lastName:String=""
    private var storeId:String=""
    lateinit var logout:LinearLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
       val view=inflater.inflate(R.layout.fragment_profile, container, false)

        editProfileButton=view.findViewById(R.id.edit_profile_button)
        profileImageUser=view.findViewById(R.id.profile_image_user)
        userName=view.findViewById(R.id.username)
        legalNameOfBusiness=view.findViewById(R.id.legal_name_of_business)
        mailId=view.findViewById(R.id.mail_id)
        textPhoneNumber=view.findViewById(R.id.text_phone_number)
        textName=view.findViewById(R.id.text_name)
        addressLine1=view.findViewById(R.id.user_address_line1)
        addressLine2=view.findViewById(R.id.user_address_line2)
        userCity=view.findViewById(R.id.user_address_city)
        userState=view.findViewById(R.id.user_address_state)
        userCountry=view.findViewById(R.id.user_address_country)
        userPincode=view.findViewById(R.id.user_address_pincode)

        logout=view.findViewById(R.id.logout)

        sharedPreferencesStatus = activity?.getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")
        logout.setOnClickListener{
            val editor = sharedPreferencesStatus!!.edit()
            editor.putInt("STATUS", 0)
            editor.apply()
            val intent=Intent(activity,Login::class.java)
            startActivity(intent)
            activity?.finish()
        }
        url+=userid?.toDouble()?.toInt().toString()
        val queue = Volley.newRequestQueue(activity as Context)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url,
            Response.Listener<JSONObject>() { response ->
                /*Once response is obtained, parse the JSON accordingly*/
                try {
                    val data = response.getJSONArray("object")

                    for (i in 0 until data.length()) {
                        val profileobject = data.getJSONObject(i)

                        val f:String=profileobject.getString("firstName")+" "+profileobject.getString("lastName")
                        firstName=profileobject.getString("firstName")
                        lastName=profileobject.getString("lastName")
                        textName.setText(f)

                        Picasso.get().load(profileobject.getString("profilePicUrl")).into(profileImageUser)
                        profilePicUrl=profileobject.getString("profilePicUrl")

                        mailId.setText(profileobject.getString("emailId"))
                        textPhoneNumber.setText("+91 "+profileobject.getString("phoneNo"))
                        userName.setText(profileobject.getString("userName"))
                       /* val sharedPref: SharedPreferences? = this.activity?.getSharedPreferences("myKey", MODE_PRIVATE)
                        val editor: SharedPreferences.Editor? = sharedPref?.edit()
                        editor?.putString("value", f)
                        editor?.apply()*/

                        val addresses=profileobject.getJSONArray("storeList")
                        val Object=addresses.getJSONObject(0)

                        legalNameOfBusiness.setText(Object.getString("legalNameOfBusiness"))
                        addressLine1.setText(Object.getString("addressLine1"))
                        addressLine2.setText(Object.getString("addressLine2"))
                        userCity.setText(Object.getString("city"))
                        userState.setText(Object.getString("addressState"))
                        userCountry.setText(Object.getString("country"))
                        userPincode.setText(Object.getString("pincode"))

                        storeId=Object.getString("id")


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


        editProfileButton.setOnClickListener{
            val intent=Intent(activity, Edit_Profile::class.java)
            intent.putExtra("userName",userName.text)
            intent.putExtra("mailId",mailId.text)
            intent.putExtra("phoneNumber",textPhoneNumber.text)
            intent.putExtra("addressLine1",addressLine1.text)
            intent.putExtra("addressLine2",addressLine2.text)
            intent.putExtra("city",userCity.text)
            intent.putExtra("country",userCountry.text)
            intent.putExtra("addressState",userState.text)
            intent.putExtra("pincode",userPincode.text)
            intent.putExtra("firstName",firstName)
            intent.putExtra("lastName",lastName)
            intent.putExtra("storeId",storeId)
            intent.putExtra("legalNameOfBusiness",legalNameOfBusiness.text)
            intent.putExtra("userImageUrl",profilePicUrl)
            intent.putExtra("userId",userid?.toDouble()?.toInt().toString())
            intent.putExtra("token",token)
            startActivity(intent)
        }

        return view
    }


}
