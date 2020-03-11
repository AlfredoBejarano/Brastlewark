package me.alfredobejarano.brastlewark

import android.os.Bundle
import android.util.Log
import android.view.WindowManager.LayoutParams
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import me.alfredobejarano.brastlewark.datasource.network.NetworkAdapter

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

        NetworkAdapter.getJSONResource({ list ->
            Log.d("GNOME", list.first().toString())
            val imageSrc = list.first().thumbnailUrl
            Log.d("IMAGE", imageSrc)
            NetworkAdapter.getBitmapFromURL(imageSrc, {
                runOnUiThread { imageView.setImageBitmap(it) }
            }, {
                it.printStackTrace()
            })
        }, {
            it.printStackTrace()
        })
    }
}
