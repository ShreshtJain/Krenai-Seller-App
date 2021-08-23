package com.krenai.vendor.Activity.Orders

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.krenai.vendor.Adapter.OrderDetailsAdapter
import com.krenai.vendor.R
import com.krenai.vendor.model.DataOrderDetails
import com.krenai.vendor.utils.jkeys.Keys.CommonResources.CONNECTION_URL_DEMAND
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.math.ceil


class Order_Details : AppCompatActivity() {
    lateinit var errortext: TextView

    private lateinit var orderDetails_recycler: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    private var orderDetailslisting = arrayListOf<DataOrderDetails>()

    private var clear: Button? = null
    lateinit var save:TextView
    lateinit  var pb:ProgressBar

    lateinit var OrderId: TextView
    lateinit var Status: TextView
    lateinit var Mode: TextView
    lateinit var updated_text: TextView

    lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>
    lateinit var bottomSheet: View

    lateinit var radiogroup: RadioGroup
    lateinit var radio_delivery: RadioButton
    lateinit var radio_ordered: RadioButton
    lateinit var radio_inprocess: RadioButton
    lateinit var radio_shipped: RadioButton

    lateinit var builder: AlertDialog.Builder

    lateinit var orderid: String
    lateinit var state: String

    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid: String? = ""
    private var token: String? = ""

    lateinit var c_id: TextView
    lateinit var order_count: TextView
    lateinit var tax: TextView
    lateinit var subtotal: TextView
    lateinit var total: TextView
    lateinit var conv: TextView
    lateinit var discount: TextView

    lateinit var emailInvoice:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order__details)

        orderid = getIntent().getStringExtra("id").toString()

        orderDetails_recycler = findViewById(R.id.order_details_recycler)

        val customername = getIntent().getStringExtra("updatedBy")

        val date = getIntent().getStringExtra("updatedDate")

        Status = findViewById(R.id.status)
        Mode = findViewById(R.id.mode)
        clear = findViewById(R.id.clear)
        save=findViewById(R.id.save)
        pb=findViewById(R.id.progress_bar)
        OrderId = findViewById(R.id.OrderId)

        OrderId.setText(orderid)
        Status.setText(getIntent().getStringExtra("description"))

        state = getIntent().getStringExtra("description").toString()

        updated_text = findViewById(R.id.updated_text)
        c_id = findViewById(R.id.c_id)
        order_count = findViewById(R.id.order_count)
        total = findViewById(R.id.total)
        subtotal = findViewById(R.id.subtotal)
        conv = findViewById(R.id.conv)
        discount = findViewById(R.id.discount)
        tax = findViewById(R.id.tax)

        bottomSheet = findViewById(R.id.bottom_sheet)
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        updated_text.setText("Updated by $customername at $date")

        radiogroup = findViewById(R.id.radiogroup)
        radio_delivery = findViewById(R.id.radio_delivery)
        radio_inprocess = findViewById(R.id.radio_inprocess)
        radio_ordered = findViewById(R.id.radio_ordered)
        radio_shipped = findViewById(R.id.radio_shipped)

        errortext = findViewById(R.id.errortext)

        emailInvoice = findViewById(R.id.email_invoice)

        builder = AlertDialog.Builder(this)
        builder.setTitle("Attention")
        builder.setMessage("Are you sure to proceed further ?")
        builder.setIcon(R.drawable.ic_action_name)

        sharedPreferencesStatus = getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid = sharedPreferencesStatus?.getString("USER_ID", "")
        token = sharedPreferencesStatus?.getString("TOKEN", "")

        builder.setNeutralButton("Cancel") { dialogInterface, which ->
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        builder.setNegativeButton("No") { dialogInterface, which ->
        }

        clear?.setOnClickListener {
            finish()
        }

        save.setOnClickListener {
            pb.visibility=View.VISIBLE

            editOrder()
        }
        setUpRecycler()

        layoutManager = LinearLayoutManager(this)

        orderDetails_recycler.addItemDecoration(
            DividerItemDecoration(
                orderDetails_recycler.context,
                (layoutManager as LinearLayoutManager).orientation
            )
        )

        Status.setOnClickListener {
            if (Status.text.toString().toUpperCase() == radio_delivery.text.toString()
                    .toUpperCase()
            ) {
                radio_delivery.setChecked(true)
                radio_shipped.setChecked(false)
                radio_ordered.setChecked(false)
                radio_inprocess.setChecked(false)
            } else if (Status.text.toString().toUpperCase() == radio_shipped.text.toString()
                    .toUpperCase()
            ) {
                radio_delivery.setChecked(false)
                radio_shipped.setChecked(true)
                radio_ordered.setChecked(false)
                radio_inprocess.setChecked(false)
            } else if (Status.text.toString().toUpperCase() == radio_ordered.text.toString()
                    .toUpperCase()
            ) {
                radio_delivery.setChecked(false)
                radio_shipped.setChecked(false)
                radio_ordered.setChecked(true)
                radio_inprocess.setChecked(false)
            } else {
                radio_delivery.setChecked(false)
                radio_shipped.setChecked(false)
                radio_ordered.setChecked(false)
                radio_inprocess.setChecked(true)
            }
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        emailInvoice.setOnClickListener {
            val detailsObject = JSONObject(getIntent().getStringExtra("object") as String)

            val builder =
                AlertDialog.Builder(this)
            val layoutInflater = layoutInflater
            val customView: View =
                layoutInflater.inflate(R.layout.invoice_dialog, null)

            builder.setView(customView)

            val name = customView.findViewById<View>(R.id.name) as TextView
            val contact = customView.findViewById<View>(R.id.contact) as TextView
            val emailId = customView.findViewById<View>(R.id.emailId) as TextView
            val textImage = customView.findViewById<MaterialButton>(R.id.text_image)

            name.text = detailsObject.getString("customerName")
            contact.text = detailsObject.getString("customerPhone")
            textImage.setText(name.text.subSequence(0, 1).toString())
            try {
                val addresses = detailsObject.getJSONArray("addresses")
                val email = addresses.getJSONObject(0).getString("emailId")
                if (email.toLowerCase() != "null") {
                    emailId.text = email
                }
            } catch (e: JSONException) {

            }
            val sendInvoice = customView.findViewById<Button>(R.id.send_invoice)
            val recipient = customView.findViewById<EditText>(R.id.recipient)
            val message = customView.findViewById<EditText>(R.id.message)
            val subject = customView.findViewById<EditText>(R.id.subject)
            val pb = customView.findViewById<ProgressBar>(R.id.progress_bar)
            pb.visibility = View.GONE

            val show=  builder.show()

            sendInvoice.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    if (!(recipient.text.toString()
                            .matches(("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$").toRegex()))
                    ) {
                        recipient.setError("Invalid Email Address")
                    }
                    else {
                        pb.visibility = View.VISIBLE

                        val url = CONNECTION_URL_DEMAND + "api/v3/store/cart/confirmation?sendInvoice=1&emailId=" + recipient.text.trim { it <= ' ' } + "&subject=Invoice"
                        val invoiceObject = detailsObject

                        val queue = Volley.newRequestQueue(this@Order_Details)
                        try {
                            val j = invoiceObject
                            val req: StringRequest = object : StringRequest(
                                Method.POST,
                                url,
                                Response.Listener { response ->
                                    pb.visibility = View.GONE

                                    Toast.makeText(
                                        this@Order_Details,
                                        response,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    if(response.equals("Success")) {
                                        show.dismiss()
                                    }
                                },
                                Response.ErrorListener {
                                    pb.visibility = View.GONE
                                    Toast.makeText(
                                        this@Order_Details ,
                                        "Some error occurred",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }) {
                                @Throws(AuthFailureError::class)
                                override fun getBody(): ByteArray {
                                    return j.toString().toByteArray()
                                }

                                override fun getBodyContentType(): String {
                                    return "application/json"
                                }
                            }
                            queue.add(req)
                        }
                        catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radio_delivery ->
                    if (checked) {
                        radio_shipped.setChecked(false)
                        radio_ordered.setChecked(false)
                        radio_inprocess.setChecked(false)
                        builder.setPositiveButton("Yes") { dialogInterface, which ->
                            Status.setText(radio_delivery.text)
                            put()
                            Toast.makeText(
                                applicationContext,
                                "Order Status Updated",
                                Toast.LENGTH_SHORT
                            ).show()
                            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                        val alertDialog: AlertDialog = builder.create()
                        alertDialog.setCancelable(false)
                        alertDialog.show()
                    }
                R.id.radio_inprocess ->
                    if (checked) {
                        radio_shipped.setChecked(false)
                        radio_ordered.setChecked(false)
                        radio_delivery.setChecked(false)
                        builder.setPositiveButton("Yes") { dialogInterface, which ->
                            Status.setText("INPROCESS")
                            put()
                            Toast.makeText(
                                applicationContext,
                                "Order Status Updated",
                                Toast.LENGTH_SHORT
                            ).show()
                            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                        val alertDialog: AlertDialog = builder.create()
                        alertDialog.setCancelable(false)
                        alertDialog.show()
                    }
                R.id.radio_ordered ->
                    if (checked) {
                        radio_delivery.setChecked(false)
                        radio_shipped.setChecked(false)
                        radio_inprocess.setChecked(false)
                        builder.setPositiveButton("Yes") { dialogInterface, which ->
                            Status.setText(radio_ordered.text)
                            put()
                            Toast.makeText(
                                applicationContext,
                                "Order Status Updated",
                                Toast.LENGTH_SHORT
                            ).show()
                            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                        val alertDialog: AlertDialog = builder.create()
                        alertDialog.setCancelable(false)
                        alertDialog.show()
                    }
                R.id.radio_shipped ->
                    if (checked) {
                        radio_ordered.setChecked(false)
                        radio_delivery.setChecked(false)
                        radio_inprocess.setChecked(false)
                        builder.setPositiveButton("Yes") { dialogInterface, which ->
                            Status.setText(radio_shipped.text)
                            put()
                            Toast.makeText(
                                applicationContext,
                                "Order Status Updated",
                                Toast.LENGTH_SHORT
                            ).show()
                            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                        val alertDialog: AlertDialog = builder.create()
                        alertDialog.setCancelable(false)
                        alertDialog.show()
                    }
            }
        }
    }

    fun setUpRecycler() {

        val detailsObject = JSONObject(getIntent().getStringExtra("object") as String)

        Log.d("Customer Data", "" + detailsObject);

        val cartlisting = detailsObject.getJSONArray("cartProductRequests")

        if (detailsObject.getString("couponDiscount") == "null") {
           discount.setText("₹ " + "0.0")
        } else {
            discount.setText("₹ " + (detailsObject.getDouble("couponDiscount")).toString())
        }

        val createDate = detailsObject.getString("createdDate")
        val source = detailsObject.getString("orderFrom")
        val mode = detailsObject.getString("paymentMode")

        Mode.setText(mode)
        updated_text.setText("On $createDate from $source and payment mode is $mode")
        order_count.setText(detailsObject.getString("customerName"))

        if (detailsObject.getString("convenienceFee") == "null") {
           conv.setText("₹ " + "0.0")
        } else {
            conv.setText(("₹ " + detailsObject.getString("convenienceFee")))
        }

        c_id.setText("#CUS- " + detailsObject.getString("customerPhone"))

        if (detailsObject.getString("tax") == "null") {
            tax.setText("₹ " + "0.0")
            subtotal.setText("₹ " + (detailsObject.getDouble("orderAmount")+discount.text.substring(2).toDouble()).toString())
        }
        else {
            tax.setText("₹ " + detailsObject.getString("tax"))
            subtotal.setText("₹ " + (detailsObject.getDouble("orderAmount")- detailsObject.getDouble("tax")+discount.text.substring(2).toDouble()).toString())
        }

        total.setText("₹ " + detailsObject.getDouble("orderAmount").toString())



        for (i in 0 until cartlisting.length()) {
            val dataorder = cartlisting.getJSONObject(i)
            try{
            if (dataorder.getJSONArray("medias").length() > 0) {
                val model = DataOrderDetails(
                    dataorder.getJSONArray("medias").getJSONObject(0).getString("mediaUrl"),
                    dataorder.getJSONObject("productListing").getJSONObject("product")
                        .getString("name"),
                    dataorder.getJSONObject("productListing").getJSONObject("product")
                        .getString("description"),
                    dataorder.getString("sellingPrice").toDouble(),
                    dataorder.getInt("quantity")
                )
                orderDetailslisting.add(model)
            }
            else{
                val model = DataOrderDetails(
                    "",
                    dataorder.getJSONObject("productListing").getJSONObject("product")
                        .getString("name"),
                    dataorder.getJSONObject("productListing").getJSONObject("product")
                        .getString("description"),
                    dataorder.getString("sellingPrice").toDouble(),
                    dataorder.getInt("quantity")
                )
                orderDetailslisting.add(model)
            }}
            catch (e: JSONException)
            {
                val model = DataOrderDetails(
                    "",
                    dataorder.getJSONObject("productListing").getJSONObject("product")
                        .getString("name"),
                    dataorder.getJSONObject("productListing").getJSONObject("product")
                        .getString("description"),
                    dataorder.getString("sellingPrice").toDouble(),
                    dataorder.getInt("quantity")
                )
                orderDetailslisting.add(model)
            }

        }

        if (orderDetailslisting.size < 1) {
            errortext.setVisibility(View.VISIBLE)
            orderDetails_recycler.setVisibility(View.GONE)
        }
        val orderDetailsListingAdapter =
            OrderDetailsAdapter(this, orderDetailslisting)
        val mLayoutManager = LinearLayoutManager(this)
        orderDetails_recycler.layoutManager = mLayoutManager
        orderDetails_recycler.adapter = orderDetailsListingAdapter
        orderDetails_recycler.setHasFixedSize(true)
    }

    private fun put() {
        val queue = Volley.newRequestQueue(this@Order_Details)
        val url =
            "https://www.outsourcecto.com/api/v3/store/cart/status?state=" + Status.text.toString() + "&cartId=" + orderid.subSequence(
                6,
                10
            ) + "&toWallet=0"

        val jsonParams = JSONObject()
        val jsonObjectRequest =
            object : JsonObjectRequest(Method.PATCH, url, jsonParams,
                Response.Listener {
                    try {

                        Log.i("RESPONSE", it.toString())

                    } catch (e: Exception) {

                        Toast.makeText(
                            this@Order_Details,
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
                    headers["Authorization"] = " Bearer " + token

                    return headers
                }
            }
        queue.add(jsonObjectRequest)
    }

    fun editOrder()
    {
        try {
            val detailsObject = JSONObject(getIntent().getStringExtra("object") as String)
            val cartProductRequests=detailsObject.getJSONArray("cartProductRequests")

            var orderAmount=0.0
            var tax=0.0
            for(i in 0  until cartProductRequests.length())
            {
                val cartObject=cartProductRequests.getJSONObject(i)
                cartObject.put("quantity",orderDetailslisting[i].quantity)
                orderAmount+=cartObject.getInt("quantity")*cartObject.getDouble("sellingPrice")
                tax+= ceil((cartObject.getJSONObject("productListing").getDouble("gst")*(cartObject.getInt("quantity")*cartObject.getDouble("sellingPrice")))/100)
            }
            detailsObject.put("orderAmount",orderAmount)
            detailsObject.put("tax",tax.toInt())

            largeLog(detailsObject.toString())

            val url= CONNECTION_URL_DEMAND+"api/v3/store/cart"
            val queue = Volley.newRequestQueue(this)
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.POST,url,detailsObject,
                    Response.Listener {
                        pb.visibility=View.GONE

                        try
                        {
                            Toast.makeText(this@Order_Details, it.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                        catch (e: Exception) {
                            Toast.makeText(
                                this@Order_Details,
                                "Incorrect Response!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }, Response.ErrorListener {
                        pb.visibility=View.GONE
  //                      Toast.makeText(this@Order_Details, it.message, Toast.LENGTH_SHORT).show()

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
        catch (e: JSONException)
        {
            pb.visibility=View.GONE
            Toast.makeText(this@Order_Details,"Some Error Occurred. Try Again ",Toast.LENGTH_SHORT).show()
        }
    }

    fun largeLog( content: String) {
        if (content.length > 4000) {
            Log.d("details", content.substring(0, 4000))
            largeLog( content.substring(4000))
        } else {
            Log.d("details", content)
        }
    }
}
