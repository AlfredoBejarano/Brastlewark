package me.alfredobejarano.brastlewark.utils

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory

fun ImageView.setRoundBitmap(bitmap: Bitmap) {
    val drawable = RoundedBitmapDrawableFactory.create(resources, bitmap).apply {
        isCircular = true
    }
    this.setImageDrawable(drawable)
}