package me.alfredobejarano.brastlewark

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout.LayoutParams
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import me.alfredobejarano.brastlewark.databinding.DialogFiltersBinding
import me.alfredobejarano.brastlewark.utils.asInt
import me.alfredobejarano.brastlewark.utils.di.Injector
import me.alfredobejarano.brastlewark.viewmodel.GnomeListViewModel

class FiltersAlertDialog : DialogFragment() {
    private lateinit var binding: DialogFiltersBinding
    private lateinit var viewModel: GnomeListViewModel
    private lateinit var factory: GnomeListViewModel.Factory

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?) =
        DialogFiltersBinding.inflate(inflater, parent, false).also {
            binding = it
            binding.closeButton.setOnClickListener { dismissAllowingStateLoss() }
            factory = Injector.getInstance(requireActivity().application)
                .provideGnomeListViewModelFactory()
            viewModel =
                ViewModelProvider(requireActivity(), factory)[GnomeListViewModel::class.java]
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRangedSettings()
        setupMultipleOptionSettings()
        setupApplyFiltersButton()
        view.setBackgroundColor(Color.BLACK)
    }

    private fun setupRangedSettings() {
        getAgeSettings()
        getHeightSettings()
        getWeightSettings()
    }

    private fun setupMultipleOptionSettings() {
        setupHairColorOptions()
        setupProfessionOptions()
    }

    private fun getAgeSettings() = viewModel.getAgeSettings().observe(this, Observer {
        binding.ageTo.setText(it.last.toString())
        binding.ageFrom.setText(it.first.toString())
    })

    private fun getHeightSettings() = viewModel.getHeightSettings().observe(this, Observer {
        binding.heightTo.setText(it.last.toString())
        binding.heightFrom.setText(it.first.toString())
    })

    private fun getWeightSettings() = viewModel.getWeightSettings().observe(this, Observer {
        binding.weightTo.setText(it.last.toString())
        binding.weightFrom.setText(it.first.toString())
    })

    private fun setupHairColorOptions() = viewModel.hairColors.sortedBy { it }.forEach {
        createCheckbox(it, binding.hairCheckBoxGroup)
    }

    private fun setupProfessionOptions() =
        viewModel.professions.map { it.replace(" T", "T") }.sortedBy { it }.forEach {
            createCheckbox(it, binding.professionsCheckBoxGroup)
        }

    private fun createCheckbox(text: String, parent: ViewGroup) {
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        val checkBox = CheckBox(context).apply {
            tag = text
            setText(text)
            setTextColor(Color.WHITE)
            buttonTintList = ContextCompat.getColorStateList(context, R.color.checkbox_state_list)
        }
        parent.addView(checkBox, params)
    }

    private fun getMultipleSelectedOptions(parent: ViewGroup): Set<String> {
        var options = setOf<String>()
        parent.forEach {
            val checkBox = it as? CheckBox
            val tag = checkBox?.tag as? String
            if (checkBox?.isChecked == true && !tag.isNullOrBlank()) {
                options = options.plus(tag)
            }
        }
        return options
    }

    private fun getRangeFromViews(minView: EditText, maxView: EditText) =
        minView.text.asInt()..maxView.text.asInt()

    private fun setupApplyFiltersButton() = binding.searchButton.setOnClickListener {
        val ageRange = getRangeFromViews(binding.ageFrom, binding.ageTo)
        val weightRange = getRangeFromViews(binding.weightFrom, binding.weightTo)
        val heightRange = getRangeFromViews(binding.heightFrom, binding.heightTo)
        val hairColors = getMultipleSelectedOptions(binding.hairCheckBoxGroup)
        val professions = getMultipleSelectedOptions(binding.professionsCheckBoxGroup)
        viewModel.filterGnomes(ageRange, heightRange, weightRange, hairColors, professions)
        dismissAllowingStateLoss()
    }
}