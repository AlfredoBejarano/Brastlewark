package me.alfredobejarano.brastlewark

import android.os.Bundle
import android.view.WindowManager.LayoutParams
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import me.alfredobejarano.brastlewark.utils.di.Injector
import me.alfredobejarano.brastlewark.viewmodel.GnomeListViewModel

class SplashActivity : AppCompatActivity() {
    private lateinit var factory: GnomeListViewModel.Factory
    private lateinit var viewModel: GnomeListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        factory = Injector.getInstance(application).provideGnomeListViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[GnomeListViewModel::class.java]

        viewModel.gnomesLiveData.observe(this, Observer {
            getGnomePicture(it.first().thumbnailUrl)
        })
        viewModel.getGnomeList()
    }

    private fun getGnomePicture(src: String) =
        viewModel.getGnomePicture(src).observe(this, Observer {
            val imageView = ImageView(this)
            windowManager.addView(imageView, LayoutParams(LayoutParams.TYPE_APPLICATION))
            imageView.setImageBitmap(it)
        })
}
