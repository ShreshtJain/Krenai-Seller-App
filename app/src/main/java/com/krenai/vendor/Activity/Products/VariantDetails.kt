package com.krenai.vendor.Activity.Products

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krenai.vendor.R
import com.krenai.vendor.utils.jkeys.Keys
import org.json.JSONArray
import org.json.JSONObject

class VariantDetails : AppCompatActivity() {
    lateinit var clear:Button
    lateinit var save: TextView
    lateinit var heading:TextView
    lateinit var add_value:ImageView
    lateinit var parentLinearLayout:LinearLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var value: EditText
    var arrayList= arrayListOf<String>()
    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""
    var variantValue=JSONArray()
    var variantValueTemp=JSONArray("[]")
    var Size=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_variant_details)
        clear=findViewById(R.id.clear)
        heading=findViewById(R.id.heading)
        add_value=findViewById(R.id.add_value)
        save=findViewById(R.id.save)
        sharedPreferencesStatus = getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        parentLinearLayout=findViewById(R.id.parentLinear)
        layoutManager = LinearLayoutManager(this)
        value=findViewById(R.id.value)
        clear.setOnClickListener {
            finish()
        }
        variantValue=JSONArray(getIntent().getStringExtra("variantValue"))
        save.setOnClickListener {
            if(getIntent().getIntExtra("New",0)==0) {
                val variantOptions = JSONArray(getIntent().getStringExtra("variantOptions"))
                variantOptions.getJSONObject(getIntent().getIntExtra("index", 0))
                    .put("variantValue", variantValueTemp)
                Log.i("variantOptions", variantOptions.toString())
                val editor = sharedPreferencesStatus!!.edit()
                editor.putString("sharedVariantOptions", variantOptions.toString())
                editor.apply()
                finish()
            }
            else
            {
                val variantOptions = JSONArray(getIntent().getStringExtra("variantOptions"))
                val Object=JSONObject()
                Object.put("variant",getIntent().getStringExtra("name").toString())
                Object.put("variantValue",variantValueTemp)
                variantOptions.put(Object)
                val editor = sharedPreferencesStatus!!.edit()
                editor.putString("sharedVariantOptions", variantOptions.toString())
                editor.apply()
                finish()
            }
        }
        heading.setText("Add values for "+getIntent().getStringExtra("name").toString())
        for(i in 0 until variantValue.length())
        {
            value.setText(variantValue.getString(i))
            onAddField()
            value.setText("")
        }
        add_value.setOnClickListener {
            if(value.text.toString().length>0)
            {
                onAddField()
                value.setText("")
            }
        }
    }
    fun onAddField() {
        val inflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(R.layout.variant_details_custom_row, parentLinearLayout, false)
        val textView:TextView=rowView.findViewById(R.id.text)
        val del:ImageView=rowView.findViewById(R.id.delF)
        textView.setText(value.text.toString())
        arrayList.add(value.text.toString())
        variantValueTemp.put(Size,value.text.toString())
        val temp=Size
        Size+=1
        parentLinearLayout.addView(rowView, parentLinearLayout.childCount - 1)
        del.setOnClickListener {
            rowView.visibility=View.GONE
            arrayList.remove(textView.text.toString())
            variantValueTemp.remove(temp)
            Size-=1
        }
    }
    fun onDelete(view: View) {
        parentLinearLayout.removeView(view.parent as ViewGroup)
    }
}
