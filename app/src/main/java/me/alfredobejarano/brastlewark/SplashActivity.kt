package me.alfredobejarano.brastlewark

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import me.alfredobejarano.brastlewark.databinding.ActivitySplashBinding
import me.alfredobejarano.brastlewark.databinding.ItemGnomeBinding
import me.alfredobejarano.brastlewark.model.Gnome
import me.alfredobejarano.brastlewark.utils.di.Injector
import me.alfredobejarano.brastlewark.viewmodel.GnomeListViewModel

class SplashActivity : AppCompatActivity() {
    private lateinit var viewModel: GnomeListViewModel
    private lateinit var binding: ActivitySplashBinding
    private lateinit var factory: GnomeListViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        requestDependencies()
        observeGnomeList()
        viewModel.getGnomeList()
    }

    private fun requestDependencies() {
        factory = Injector.getInstance(application).provideGnomeListViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[GnomeListViewModel::class.java]
    }

    private fun observeGnomeList() = viewModel.gnomesLiveData.observe(this, Observer {
        binding.gnomesListView.adapter = null
        binding.gnomesListView.adapter = GnomeListAdapter(this, it)
    })

    private fun getGnomeProfilePicture(src: String, iv: ImageView) =
        viewModel.getGnomePicture(src).observe(this, Observer { iv.setImageBitmap(it) })

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        setupSearchView(menu?.findItem(R.id.action_search)?.actionView as? SearchView)
        return true
    }

    private fun setupSearchView(searchView: SearchView?) = searchView?.run {
        isFocusable = true
        isIconified = false
        isIconifiedByDefault = false
        queryHint = getString(R.string.search_gnome_name)
        setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) =
                viewModel.searchForGnomeByName(query ?: "").let { true }

            override fun onQueryTextChange(newText: String?) =
                viewModel.searchForGnomeByName(newText ?: "").let { true }
        })
    }

    inner class GnomeListAdapter(private val ctx: Context, private val gnomes: List<Gnome>) :
        ArrayAdapter<Gnome>(ctx, R.layout.item_gnome, gnomes) {

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup) =
            ItemGnomeBinding.inflate(LayoutInflater.from(ctx), parent, false).apply {
                val gnomeAtPosition = gnomes[position]
                gnome = gnomeAtPosition
                professions.text = gnomeAtPosition.professions.toString()
                    .replace("[", "")
                    .replace(", ]", "")
                    .replace("]", "")
                getGnomeProfilePicture(gnomeAtPosition.thumbnailUrl, gnomeProfilePicture)
            }.root
    }
}
