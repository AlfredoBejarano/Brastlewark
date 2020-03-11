package me.alfredobejarano.brastlewark.repository

import me.alfredobejarano.brastlewark.datasource.local.GnomeDataBaseHelper
import me.alfredobejarano.brastlewark.datasource.local.SharedPreferencesDataSource
import me.alfredobejarano.brastlewark.datasource.network.GnomeApiService
import me.alfredobejarano.brastlewark.model.Gnome

class GnomeRepository(
    private val remoteDataSource: GnomeApiService,
    private val localDataSource: GnomeDataBaseHelper,
    private val sharedPreferencesDataSource: SharedPreferencesDataSource
) {
    private fun getGnomesByNetwork(onComplete: (result: Pair<List<Gnome>?, Exception?>) -> Unit) =
        remoteDataSource.getJSONResource({
            it.forEach(localDataSource::create)
            onComplete(Pair(it, null))
        }, {
            onComplete(Pair(null, it))
        })

    private fun getGnomesByCache(onComplete: (result: Pair<List<Gnome>?, Exception?>) -> Unit) {
        try {
            Pair(localDataSource.getAllGnomes(), null)
        } catch (e: Exception) {
            Pair(null, e)
        }
    }

    fun getGnomes(onComplete: (result: Pair<List<Gnome>?, Exception?>) -> Unit) =
        if (sharedPreferencesDataSource.isGnomeCacheValid()) {
            getGnomesByCache(onComplete)
        } else {
            getGnomesByNetwork(onComplete)
        }
}