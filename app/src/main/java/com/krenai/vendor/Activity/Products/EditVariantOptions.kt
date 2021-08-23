package com.krenai.vendor.Activity.Products

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krenai.vendor.Adapter.VariantAdapter
import com.krenai.vendor.R
import org.json.JSONArray

class EditVariantOptions : AppCompatActivity() {
    private var sharedPreferencesStatus: SharedPreferences? = null
    private var userid:String? = ""
    private var token:String? = ""
    lateinit var OptionsRecycler: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var clear: Button
    lateinit var save: TextView
    lateinit var variantOptions: JSONArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_variant_options)
        sharedPreferencesStatus = getSharedPreferences(
            "loginStatus",
            Context.MODE_PRIVATE
        )
        userid=sharedPreferencesStatus?.getString("USER_ID","")
        token=sharedPreferencesStatus?.getString("TOKEN","")
        OptionsRecycler=findViewById(R.id.edit_options_recycler)
        clear=findViewById(R.id.clear)
        save=findViewById(R.id.save)
        variantOptions=JSONArray(getIntent().getStringExtra("variantOptions"))
        val editor = sharedPreferencesStatus!!.edit()
        editor.putString("sharedVariantOptions",variantOptions.toString())
        editor.apply()
        layoutManager = LinearLayoutManager(this)
        OptionsRecycler.addItemDecoration(
            DividerItemDecoration    (
              OptionsRecycler.context,
                (layoutManager as LinearLayoutManager).orientation
            )
        )
        save.setOnClickListener {
            val sharedVariantOptions=sharedPreferencesStatus?.getString("sharedVariantOptions","")
            val jsonVariantOptions=JSONArray(sharedVariantOptions)
            val jsonVariantOptionsFinal=JSONArray()
            var index=0
            for (i in 0 until jsonVariantOptions.length())
            {
                if(!(jsonVariantOptions.getJSONObject(i).getJSONArray("variantValue").length()==0))
                {
                    jsonVariantOptionsFinal.put(index,jsonVariantOptions.getJSONObject(i))
                    index+=1
                }
            }
            Log.i("variantOptions",jsonVariantOptionsFinal.toString())
            var intent= Intent(this,ProductUpdate::class.java)
            intent.putExtra("variantOptions",jsonVariantOptionsFinal.toString())
            intent.putExtra("starter","starter")
            startActivity(intent)
            finish()
        }
        setUpRecycler()
    }
    fun setUpRecycler()
    {
        var listing= arrayListOf<String>()
        if(variantOptions.length()>0)
        {
            for (i in 0 until variantOptions.length())
            {
                listing.add(variantOptions.getJSONObject(i).getString("variant"))
            }
            val Adapter =
                VariantAdapter(this,listing,variantOptions,0)
            val mLayoutManager = LinearLayoutManager(this)
            OptionsRecycler.layoutManager = mLayoutManager
            OptionsRecycler.adapter = Adapter
            OptionsRecycler.setHasFixedSize(true)
        }

    }
}
