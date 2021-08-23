package com.krenai.vendor.Activity.Orders


import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.krenai.vendor.Activity.calendar_activity
import com.krenai.vendor.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class Search_Orders: AppCompatActivity(), DatePickerDialog.OnDateSetListener{

    lateinit var back: Button
    lateinit var search: Button
    lateinit var check_in:LinearLayout
    lateinit var check_indate:TextView
    lateinit var check_inday:TextView
    lateinit var dateFormat: SimpleDateFormat
    lateinit var check_inmonth:TextView
    lateinit var check_outmonth:TextView
    lateinit var check_outday:TextView
    lateinit var check_outdate:TextView
    lateinit var check_out:LinearLayout

    lateinit var s:String

    val builder = MaterialDatePicker.Builder.dateRangePicker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search__orders)

        back = findViewById(R.id.back)
        search = findViewById(R.id.search)
        check_in = findViewById(R.id.check_in)
        check_out = findViewById(R.id.check_out)


        back.setOnClickListener {
            finish()
        }
        check_in.setOnClickListener {
            val newFragment: DialogFragment = calendar_activity()
            newFragment.show(supportFragmentManager, "DatePicker")
            s = "check_in"

        }
        check_out.setOnClickListener {
            val newFragment: DialogFragment = calendar_activity()
            newFragment.show(supportFragmentManager, "DatePicker")
            s = "check_out"

        }
    }
      override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
            var a:Calendar = Calendar.getInstance()
            var b:Calendar = Calendar.getInstance()
            var c:Calendar = Calendar.getInstance();


            a.set(Calendar.YEAR, year);
            b.set(Calendar.MONTH, month)
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth)


            if(s=="check_in") {

                check_indate = findViewById(R.id.check_indate)
                check_inday = findViewById(R.id.check_inday)
                check_inmonth = findViewById(R.id.check_inmonth)
                val currentDateString =
                    DateFormat.getDateInstance(DateFormat.FULL).format(c.time)
                var m = SimpleDateFormat("EEEE").format(c.time)
                var y = SimpleDateFormat("MMM ").format(b.getTime())

                check_indate.setText("$dayOfMonth")
                check_inmonth.setText(y + "$year")
                check_inday.setText(m)
            }
          else if(s=="check_out")
            {

                check_outdate = findViewById(R.id.check_outdate)
                check_outday = findViewById(R.id.check_outday)
                check_outmonth = findViewById(R.id.check_outmonth)
                val currentDateString =
                    DateFormat.getDateInstance(DateFormat.FULL).format(c.time)
                var m=SimpleDateFormat("EEEE").format(c.time)
                var y=SimpleDateFormat("MMM ").format(b.getTime())

                check_outdate.setText("$dayOfMonth")
                check_outmonth.setText(y+"$year")
                check_outday.setText(m)

            }
        }

    }
