package me.alfredobejarano.brastlewark.datasource.local

import android.app.Application
import android.content.Context
import java.util.*

class SharedPreferencesDataSource(private val application: Application) {
    private companion object {
        const val GNOME_CACHE_KEY = "gnome_cahe"
        const val PICTURES_CACHE = "pictures_cache"
        const val SHARED_FILE_NAME = "cache"
        const val CACHE_DURATION = 1 // Cache for 1 day.
    }

    private val preferences by lazy {
        application.getSharedPreferences(GNOME_CACHE_KEY, Context.MODE_PRIVATE)
    }

    private fun generateCacheTimeStamp() = Calendar.getInstance(Locale.getDefault()).run {
        add(Calendar.DAY_OF_YEAR, CACHE_DURATION)
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

    fun isPictureCacheValid() = isCacheValid(PICTURES_CACHE)
    fun generatePictureCache() = storeCacheTimeStamp(PICTURES_CACHE)
}