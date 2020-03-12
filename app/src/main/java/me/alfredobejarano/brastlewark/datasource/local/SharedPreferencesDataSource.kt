package me.alfredobejarano.brastlewark.datasource.local

import android.app.Application
import android.content.Context
import java.util.Calendar
import java.util.Locale

class SharedPreferencesDataSource(private val application: Application) {
    private companion object {
        const val GNOME_CACHE_KEY = "gnome_cahe"
        const val PICTURES_CACHE = "pictures_cache"
        const val SHARED_FILE_NAME = "cache"
        const val CACHE_DURATION = 15 // Cache for 15 minutes.
    }

    private val preferences by lazy {
        application.getSharedPreferences(SHARED_FILE_NAME, Context.MODE_PRIVATE)
    }

    private fun generateCacheTimeStamp() = Calendar.getInstance(Locale.getDefault()).run {
        add(Calendar.MINUTE, CACHE_DURATION)
        timeInMillis
    }

    private fun getCurrentTimeInMillis() = Calendar.getInstance(Locale.getDefault()).timeInMillis

    private fun storeCacheTimeStamp(key: String) = preferences.edit()?.apply {
        putLong(key, generateCacheTimeStamp())
    }?.apply()

    private fun isCacheValid(key: String): Boolean {
        val currentTimeInMillis = getCurrentTimeInMillis()
        val cacheTimeInMillis = preferences.getLong(key, currentTimeInMillis)
        return currentTimeInMillis < cacheTimeInMillis
    }

    fun isGnomeCacheValid() = isCacheValid(GNOME_CACHE_KEY)
    fun generateGnomeCache() = storeCacheTimeStamp(GNOME_CACHE_KEY)

    fun isPictureCacheValid(picName: String) = isCacheValid("${PICTURES_CACHE}_$picName")
    fun generatePictureCache(picName: String) = storeCacheTimeStamp("${PICTURES_CACHE}_$picName")
}