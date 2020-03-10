package me.alfredobejarano.brastlewark

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import me.alfredobejarano.brastlewark.datasource.network.NetworkAdapter

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NetworkAdapter.getJSONResource({
            it.forEachIndexed { index, gnome ->
                Log.d("Gnome #$index", gnome.toString())
            }
        }, {
            it.printStackTrace()
        })
    }
}
