package me.alfredobejarano.brastlewark.utils

import android.text.Editable

fun Editable?.asInt() = try {
    this?.toString()?.toInt() ?: 0
} catch (e: Exception) {
    0
}