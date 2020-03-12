package me.alfredobejarano.brastlewark

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.LinearLayout.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import me.alfredobejarano.brastlewark.databinding.ActivityGnomeBinding
import me.alfredobejarano.brastlewark.utils.di.Injector
import me.alfredobejarano.brastlewark.utils.setRoundBitmap
import me.alfredobejarano.brastlewark.viewmodel.GnomeDetailsViewModel

class GnomeActivity : AppCompatActivity() {
    companion object {
        const val GNOME_NAME_KEY = "${BuildConfig.APPLICATION_ID}.GNOME_NAME_KEY"
        private const val HAIR_COLOR_GRAY = "Gray"
        private const val HAIR_COLOR_GREEN = "Green"
        private const val HAIR_COLOR_PINK = "Pink"
        private const val HAIR_COLOR_RED = "Red"
    }

    private lateinit var viewModel: GnomeDetailsViewModel
    private lateinit var databinding: ActivityGnomeBinding
    private lateinit var factory: GnomeDetailsViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestDependencies()
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_gnome)
        intent?.extras?.getString(GNOME_NAME_KEY)?.let(::getGnome) ?: run { finish() }
    }

    private fun getGnome(name: String) = viewModel.getGnome(name).observe(this, Observer {
        databinding.apply {
            val ctx = root.context
            gnome = it
            gnomeHairColor.compoundDrawables.filterNotNull().run {
                if (isNotEmpty()) setGnomeHairColor(first(), it.hairColor)
            }
            getGnomePicture(it?.thumbnailUrl ?: "")
            it.professions.forEach {
                addTextView(it.replace(" T", ""), gnomeProfessionsList)
            }
            it.friends.forEach {
                addTextView(it, gnomeFriendsList)
            }
            setFriendsClickListeners()
        }
    })


    private fun addTextView(text: String, parent: LinearLayout) {
        TextView(this).apply {
            val params = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            val textView = TextView(this@GnomeActivity).apply {
                setText(text)
                setTextColor(Color.WHITE)
                alpha = 0.7f
                textSize = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP,
                    6f,
                    resources.displayMetrics
                )
            }
            parent.addView(textView, params)
        }
    }

    private fun setFriendsClickListeners() = databinding.gnomeFriendsList.forEach {
        val textView = it as? TextView
        val name = textView?.text?.toString() ?: ""
        if (name.isNotEmpty()) {
            textView?.setOnClickListener {
                openGnomeDetailsActivity(name)
            }
        }
    }

    private fun setGnomeHairColor(drawable: Drawable?, hairColor: String) = drawable?.run {
        val color = getGnomeHairColor(hairColor)
        if (color != 0) {
            setTint(color)
        }
    }

    private fun getGnomePicture(src: String) = viewModel.getGnomePicture(src).observe(
        this,
        Observer(databinding.gnomeProfilePicture::setRoundBitmap)
    )

    private fun requestDependencies() {
        factory = Injector.getInstance(application).provideGnomeDetailsViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[GnomeDetailsViewModel::class.java]
    }

    private fun getGnomeHairColor(color: String) = when (color) {
        HAIR_COLOR_GRAY -> Color.GRAY
        HAIR_COLOR_GREEN -> Color.GREEN
        HAIR_COLOR_PINK -> Color.MAGENTA
        HAIR_COLOR_RED -> Color.RED
        else -> 0
    }

    private fun openGnomeDetailsActivity(gnomeName: String) = startActivity(
        Intent(
            this,
            GnomeActivity::class.java
        ).putExtra(GnomeActivity.GNOME_NAME_KEY, gnomeName)
    )
}
