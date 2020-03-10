package me.alfredobejarano.brastlewark.utils

import android.graphics.Bitmap
import android.graphics.Matrix

/**
 * Returns a scaled Bitmap with the new dimensions.
 * @param newWidth The resized bitmap width (in pixels).
 * @param newHeight The resized bitmap height (in pixels).
 */
fun Bitmap.resize(newWidth: Int, newHeight: Int): Bitmap {
    val oldWidth = width
    val oldHeight = height

    val widthScale = (newWidth / oldWidth).toFloat()
    val heightScale = (newHeight / oldHeight).toFloat()

    val scaleMatrix = Matrix().apply {
        postScale(widthScale, heightScale)
    }

    return Bitmap.createBitmap(this, 0, 0, oldWidth, newHeight, scaleMatrix, false)
}
