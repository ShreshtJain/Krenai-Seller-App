package com.krenai.vendor.Interface

import android.view.MotionEvent


interface onActivityTouchListener {
    fun getTouchCoordinates(ev: MotionEvent?)
}