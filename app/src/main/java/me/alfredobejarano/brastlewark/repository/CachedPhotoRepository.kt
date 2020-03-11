package me.alfredobejarano.brastlewark.repository

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import me.alfredobejarano.brastlewark.datasource.local.CachedPhotoDataBaseHelper
import me.alfredobejarano.brastlewark.datasource.local.SharedPreferencesDataSource
import me.alfredobejarano.brastlewark.datasource.network.GnomeApiService
import me.alfredobejarano.brastlewark.utils.getFileNameFromURL
import me.alfredobejarano.brastlewark.utils.runOnWorkerThread
import java.io.File
import java.io.FileOutputStream

class CachedPhotoRepository(
    private val app: Application,
    private val remoteDataSource: GnomeApiService,
    private val localDataSource: CachedPhotoDataBaseHelper,
    private val sharedPreferencesDataSource: SharedPreferencesDataSource
) {
    private fun storeBitmapInCache(bitmap: Bitmap, src: String) = try {
        val filePath = File(app.cacheDir, "cached-${src.getFileNameFromURL()}").path
        val outputStream = FileOutputStream(filePath)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        localDataSource.create(src, filePath)
        sharedPreferencesDataSource.generatePictureCache()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    private fun getPictureByNetwork(src: String, onComplete: (Pair<Bitmap?, Exception?>) -> Unit) =
        remoteDataSource.getBitmapFromURL(src, {
            storeBitmapInCache(it, src)
            onComplete(Pair(it, null))
        }, {
            onComplete(Pair(null, it))
        })

    private fun getPictureByCache(src: String, onComplete: (Pair<Bitmap?, Exception?>) -> Unit) =
        try {
            val path = localDataSource.getCachedPicture(src)
            val bitmap = BitmapFactory.decodeFile(path)
            onComplete(Pair(bitmap, null))
        } catch (e: Exception) {
            onComplete(Pair(null, e))
        }

    fun getPicture(src: String, onComplete: (Pair<Bitmap?, Exception?>) -> Unit) =
        runOnWorkerThread {
            if (sharedPreferencesDataSource.isPictureCacheValid()) {
                getPictureByCache(src, onComplete)
            } else {
                getPictureByNetwork(src, onComplete)
            }
        }
}