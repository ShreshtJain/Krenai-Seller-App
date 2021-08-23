package com.krenai.vendor.Activity.Authentication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.krenai.vendor.Adapter.IntroSliderAdapter
import com.krenai.vendor.R
import com.thesimplycoder.simpleintroslider.Intro1Fragment
import com.thesimplycoder.simpleintroslider.Intro2Fragment
import com.thesimplycoder.simpleintroslider.Intro3Fragment
import kotlinx.android.synthetic.main.activity_intro_slider.*


class IntroSliderActivity : AppCompatActivity() {

    private val fragmentList = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // making the status bar transparent

       // updateScreenResolution()

        setContentView(R.layout.activity_intro_slider)

        val adapter = IntroSliderAdapter(this)
        vpIntroSlider.adapter = adapter

        fragmentList.addAll(listOf(
            Intro1Fragment(), Intro2Fragment(), Intro3Fragment()
        ))
        adapter.setFragmentList(fragmentList)

        indicatorLayout.setIndicatorCount(adapter.itemCount)
        indicatorLayout.selectCurrentPosition(0)

        registerListeners()
    }
    private fun updateScreenResolution() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window
            w.addFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

            )
        }
    }

    private fun registerListeners() {
        vpIntroSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                indicatorLayout.selectCurrentPosition(position)

                if (position < fragmentList.lastIndex) {
                    tvSkip.visibility = View.VISIBLE
                    tvNext.text = "Next"
                } else {
                    tvSkip.visibility = View.GONE
                    tvNext.text = "DONE"
                }
            }
        })

        tvSkip.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        tvNext.setOnClickListener {
            val position = vpIntroSlider.currentItem

            if (position < fragmentList.lastIndex) {
                vpIntroSlider.currentItem = position + 1
            } else {
                startActivity(Intent(this, Login::class.java))
                finish()
            }
        }

    }
}
