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
import me.alfredobejarano.brastlewark.utils.map
import me.alfredobejarano.brastlewark.utils.runOnWorkerThread

class GnomeListViewModel(
    private val gnomeRepository: GnomeRepository,
    private val cachedPhotoRepository: CachedPhotoRepository
) : ViewModel() {

    private val gnomes = mutableListOf<Gnome>()
    private val gnomesMutableLiveData = MutableLiveData<List<Gnome>>()
    val gnomesLiveData = gnomesMutableLiveData as LiveData<List<Gnome>>

    fun getGnomeList() = runOnWorkerThread {
        if (gnomes.isEmpty()) {
            gnomeRepository.getGnomes {
                gnomes.addAll(it.first ?: emptyList())
                gnomes.sortBy { gnome -> gnome.name }
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
        gnomes.sortBy { it.weight }
        gnomes.first().height.toInt()..gnomes.last().height.toInt()
    }

    fun getWeightSettings() = forLiveData {
        gnomes.sortBy { it.weight }
        gnomes.first().weight.toInt()..gnomes.last().weight.toInt()
    }

    fun getHairColors() = forLiveData {
        val colors = setOf<String>()
        gnomes.forEach { colors.plus(it.hairColor) }
        colors
    }

    fun filterByProfession(profession: String) =
        gnomesMutableLiveData.map { gnomes.filter { it.professions.contains(profession) } }

    fun filterByName(name: String) = gnomesMutableLiveData.map { gnomes.filter { it.name == name } }

    fun filterByAge(minAge: Int, maxAge: Int) =
        gnomesMutableLiveData.map { gnomes.filter { it.age in minAge..maxAge } }

    fun filterByHeight(minHeight: Int, maxHeight: Int) =
        gnomesMutableLiveData.map { gnomes.filter { it.height.toInt() in minHeight..maxHeight } }

    fun filterByWeight(minWeight: Int, maxWeight: Int) =
        gnomesMutableLiveData.map { gnomes.filter { it.weight.toInt() in minWeight..maxWeight } }

    fun filterByHairColor(color: String) =
        gnomesMutableLiveData.map { gnomes.filter { it.hairColor == color } }

    fun searchForGnomeByName(query: String) = gnomesMutableLiveData.map {
        if (query.isBlank()) {
            gnomes
        } else {
            gnomes.filter { it.name.contains(query, true) }
        }
    }

    fun getGnomePicture(src: String) = MutableLiveData<Bitmap>().apply {
        cachedPhotoRepository.getPicture(src) { postValue(it.first) }
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