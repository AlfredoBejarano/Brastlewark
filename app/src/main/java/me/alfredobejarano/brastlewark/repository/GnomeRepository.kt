package me.alfredobejarano.brastlewark.repository

import me.alfredobejarano.brastlewark.datasource.local.GnomeDataBaseHelper
import me.alfredobejarano.brastlewark.datasource.local.SharedPreferencesDataSource
import me.alfredobejarano.brastlewark.datasource.network.GnomeApiService
import me.alfredobejarano.brastlewark.model.Gnome
import me.alfredobejarano.brastlewark.utils.runOnWorkerThread

class GnomeRepository(
    private val remoteDataSource: GnomeApiService,
    private val localDataSource: GnomeDataBaseHelper,
    private val sharedPreferencesDataSource: SharedPreferencesDataSource
) {
    private fun getGnomesByNetwork(onComplete: (result: Pair<List<Gnome>?, Exception?>) -> Unit) =
        remoteDataSource.getJSONResource({
            it.forEach(localDataSource::create)
            sharedPreferencesDataSource.generateGnomeCache()
            onComplete(Pair(it, null))
        }, {
            onComplete(Pair(null, it))
        })

    private fun getGnomesByCache(onComplete: (result: Pair<List<Gnome>?, Exception?>) -> Unit) =
        try {
            onComplete(Pair(localDataSource.getAllGnomes(), null))
        } catch (e: Exception) {
            onComplete(Pair(null, e))
        }

    fun getGnomes(onComplete: (result: Pair<List<Gnome>?, Exception?>) -> Unit) =
        runOnWorkerThread {
            if (sharedPreferencesDataSource.isGnomeCacheValid()) {
                getGnomesByCache(onComplete)
            } else {
                getGnomesByNetwork(onComplete)
            }
        }

    fun getGnomeByName(name: String, onComplete: (result: Pair<Gnome?, Exception?>) -> Unit) =
        runOnWorkerThread {
            try {
                onComplete(Pair(localDataSource.getGnomeByName(name), null))
            } catch (e: Exception) {
                onComplete(Pair(null, e))
            }
        }
}