package com.krenai.vendor

import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.krenai.vendor.Activity.MainActivity

class Complete : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete)
        val mImgCheck =
            findViewById<ImageView>(R.id.imageView)
        (mImgCheck.drawable as Animatable).start()
        Handler().postDelayed({
            startActivity(Intent(this@Complete, MainActivity::class.java))
            finish()
            finishAffinity()
        }, SPLASH_TIME_OUT.toLong())
    }

    companion object {
        private const val SPLASH_TIME_OUT = 3000
    }
}
