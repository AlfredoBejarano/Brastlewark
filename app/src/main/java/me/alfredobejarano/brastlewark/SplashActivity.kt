package me.alfredobejarano.brastlewark

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import me.alfredobejarano.brastlewark.databinding.ActivitySplashBinding
import me.alfredobejarano.brastlewark.databinding.ItemGnomeBinding
import me.alfredobejarano.brastlewark.model.Gnome
import me.alfredobejarano.brastlewark.utils.asCleanString
import me.alfredobejarano.brastlewark.utils.createBitmap
import me.alfredobejarano.brastlewark.utils.di.Injector
import me.alfredobejarano.brastlewark.utils.observeWith
import me.alfredobejarano.brastlewark.utils.runOnWorkerThread
import me.alfredobejarano.brastlewark.utils.setRoundBitmap
import me.alfredobejarano.brastlewark.viewmodel.GnomeListViewModel

class SplashActivity : AppCompatActivity() {
    private lateinit var viewModel: GnomeListViewModel
    private lateinit var binding: ActivitySplashBinding
    private lateinit var factory: GnomeListViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        requestDependencies()
        observeErrors()
        observeGnomeList()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setLogo(R.drawable.ic_filter_list_white_24dp)
        viewModel.getGnomeList()
    }

    private fun observeErrors() = Events.errorLiveData.observe(this, Observer {
        it?.run {
            Toast.makeText(this@SplashActivity, localizedMessage, Toast.LENGTH_SHORT).show()
        }
    })

    private fun requestDependencies() {
        factory = Injector.getInstance(application).provideGnomeListViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[GnomeListViewModel::class.java]
    }

    private fun observeGnomeList() = viewModel.gnomesLiveData.observeWith(this, {
        binding.gnomesListView.apply {
            adapter = null
            adapter = GnomeListAdapter(this@SplashActivity, it)
            setOnItemClickListener { _, _, position, _ -> openGnomeDetailsActivity(it[position].name) }
        }
        binding.splashLoading.visibility = View.GONE
    })

    private fun openGnomeDetailsActivity(gnomeName: String) = startActivity(
        Intent(
            this,
            GnomeActivity::class.java
        ).putExtra(GnomeActivity.GNOME_NAME_KEY, gnomeName)
    )

    private fun getGnomeProfilePicture(gnome: Gnome, iv: ImageView, pb: ProgressBar) =
        viewModel.getGnomePicture(gnome.thumbnailUrl).observeWith(this, {
            iv.setRoundBitmap(it)
            pb.visibility = View.GONE
        }, {
            runOnWorkerThread {
                val bitmap = createBitmap(gnome.name)
                runOnUiThread {
                    iv.setRoundBitmap(bitmap)
                    pb.visibility = View.GONE
                }
            }
        })

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        setupSearchView(menu?.findItem(R.id.action_search)?.actionView as? SearchView)
        return true
    }

    private fun setupSearchView(searchView: SearchView?) = searchView?.run {
        queryHint = getString(R.string.search_gnome_name)
        setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) =
                viewModel.searchForGnomeByName(query ?: "").let { true }

            override fun onQueryTextChange(newText: String?) =
                viewModel.searchForGnomeByName(newText ?: "").let { true }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_filter) {
            launchFilterDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun launchFilterDialog() =
        FiltersAlertDialog().show(supportFragmentManager, FiltersAlertDialog::class.java.simpleName)

    inner class GnomeListAdapter(private val ctx: Context, private val gnomes: List<Gnome>) :
        ArrayAdapter<Gnome>(ctx, R.layout.item_gnome, gnomes) {

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup) =
            ItemGnomeBinding.inflate(LayoutInflater.from(ctx), parent, false).apply {
                val gnomeAtPosition = gnomes[position]
                gnome = gnomeAtPosition
                professions.text = gnomeAtPosition.professions.asCleanString()
                gnomeLoading.visibility = View.GONE
                getGnomeProfilePicture(gnomeAtPosition, gnomeProfilePicture, pictureProgress)
            }.root
    }
}
