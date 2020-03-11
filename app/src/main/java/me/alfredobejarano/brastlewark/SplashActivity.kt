package me.alfredobejarano.brastlewark

import android.os.Bundle
import android.util.Log
import android.view.WindowManager.LayoutParams
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import me.alfredobejarano.brastlewark.datasource.local.BrastlewarkDatabase
import me.alfredobejarano.brastlewark.datasource.local.SharedPreferencesDataSource
import me.alfredobejarano.brastlewark.datasource.network.GnomeApiService
import me.alfredobejarano.brastlewark.repository.CachedPhotoRepository
import me.alfredobejarano.brastlewark.repository.GnomeRepository

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageView = ImageView(this)
        windowManager?.addView(
            imageView, LayoutParams(
                LayoutParams.TYPE_APPLICATION,
                LayoutParams.FLAG_NOT_FOCUSABLE
            )
        )

        val sharedPreferencesDataSource = SharedPreferencesDataSource(application)
        val gnomeApiService = GnomeApiService
        val database = BrastlewarkDatabase.getInstance(application)
        val gnomeDataBaseHelper = database.getGnomeDataBaseHelper()
        val cachedPhotoDataBaseHelper = database.getCachedPhotoDataBaseHelper()

        val gnomeRepo =
            GnomeRepository(gnomeApiService, gnomeDataBaseHelper, sharedPreferencesDataSource)
        val pictureRepo = CachedPhotoRepository(
            application,
            gnomeApiService,
            cachedPhotoDataBaseHelper,
            sharedPreferencesDataSource
        )

        gnomeRepo.getGnomes { result ->
            result.first?.let { list ->
                Log.d("GNOME", list.first().toString())
                val imageSrc = list.first().thumbnailUrl
                pictureRepo.getPicture(imageSrc) { photoResult ->
                    photoResult.first?.let {
                        Log.d("Bitmap", it.toString())
                        runOnUiThread { imageView.setImageBitmap(it) }
                    } ?: run {
                        result.second?.printStackTrace()
                    }
                }
            } ?: run {
                result.second?.printStackTrace()
            }
        }
    }
}
