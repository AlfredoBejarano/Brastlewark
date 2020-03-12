package me.alfredobejarano.brastlewark.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.alfredobejarano.brastlewark.model.Gnome
import me.alfredobejarano.brastlewark.repository.CachedPhotoRepository
import me.alfredobejarano.brastlewark.repository.GnomeRepository
import me.alfredobejarano.brastlewark.utils.forLiveData
import me.alfredobejarano.brastlewark.utils.has
import me.alfredobejarano.brastlewark.utils.map
import me.alfredobejarano.brastlewark.utils.runOnWorkerThread

class GnomeListViewModel(
    private val gnomeRepository: GnomeRepository,
    private val cachedPhotoRepository: CachedPhotoRepository
) : ViewModel() {

    var hairColors = setOf<String>()
    var professions = setOf<String>()

    private val gnomes = mutableListOf<Gnome>()
    private val gnomesMutableLiveData = MutableLiveData<List<Gnome>>()
    val gnomesLiveData = gnomesMutableLiveData as LiveData<List<Gnome>>

    fun getGnomeList() = runOnWorkerThread {
        if (gnomes.isEmpty()) {
            gnomeRepository.getGnomes {
                gnomes.addAll(it.first ?: emptyList())
                gnomes.sortBy { gnome -> gnome.name }
                gnomes.forEach { gnome ->
                    hairColors = hairColors.plus(gnome.hairColor)
                    professions = professions.plus(gnome.professions)
                }
                gnomesMutableLiveData.postValue(gnomes)
            }
        } else {
            gnomesMutableLiveData.postValue(gnomes)
        }
    }

    fun getAgeSettings() = forLiveData {
        gnomes.sortBy { it.age }
        gnomes.first().age..gnomes.last().age
    }

    fun getHeightSettings() = forLiveData {
        gnomes.sortBy { it.height }
        gnomes.first().height..gnomes.last().height
    }

    fun getWeightSettings() = forLiveData {
        gnomes.sortBy { it.weight }
        gnomes.first().weight..gnomes.last().weight
    }

    fun filterGnomes(
        ageRange: IntRange,
        heightRange: IntRange,
        weightRange: IntRange,
        hairColors: Set<String>,
        professions: Set<String>
    ) = gnomesMutableLiveData.map {
        gnomes.filter {
            val ageInRange = ageRange.has(it.age)
            val weightInRange = weightRange.has(it.weight)
            val heightIntRange = heightRange.has(it.height)
            val withHairColor =
                if (hairColors.isEmpty()) true else hairColors.contains(it.hairColor)
            val withProfessions =
                if (professions.isEmpty()) true else it.professions.containsAll(professions)
            ageInRange && weightInRange && heightIntRange && withHairColor && withProfessions
        }.sortedBy { it.name }
    }

    fun searchForGnomeByName(query: String) = gnomesMutableLiveData.map {
        if (query.isBlank()) {
            gnomes
        } else {
            gnomes.filter { it.name.contains(query, true) }
        }
    }

    fun getGnomePicture(src: String) = MutableLiveData<Bitmap>().apply {
        runOnWorkerThread { cachedPhotoRepository.getPicture(src) { postValue(it.first) } }
    } as LiveData<Bitmap>

    class Factory(
        private val repository: GnomeRepository,
        private val photoRepository: CachedPhotoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            GnomeListViewModel(repository, photoRepository) as T
    }
}
