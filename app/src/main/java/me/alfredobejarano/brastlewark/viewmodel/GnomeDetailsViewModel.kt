package me.alfredobejarano.brastlewark.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.alfredobejarano.brastlewark.model.Gnome
import me.alfredobejarano.brastlewark.repository.CachedPhotoRepository
import me.alfredobejarano.brastlewark.repository.GnomeRepository
import me.alfredobejarano.brastlewark.utils.runOnWorkerThread

class GnomeDetailsViewModel(
    private val gnomeRepository: GnomeRepository,
    private val cachedPhotoRepository: CachedPhotoRepository
) : ViewModel() {

    fun getGnome(name: String) = MutableLiveData<Gnome>().apply {
        runOnWorkerThread { gnomeRepository.getGnomeByName(name) { postValue(it.first) } }
    } as LiveData<Gnome>

    fun getGnomePicture(src: String) = MutableLiveData<Bitmap>().apply {
        runOnWorkerThread { cachedPhotoRepository.getPicture(src) { postValue(it.first) } }
    } as LiveData<Bitmap>

    class Factory(
        private val repository: GnomeRepository,
        private val photoRepository: CachedPhotoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            GnomeDetailsViewModel(repository, photoRepository) as T
    }
}
