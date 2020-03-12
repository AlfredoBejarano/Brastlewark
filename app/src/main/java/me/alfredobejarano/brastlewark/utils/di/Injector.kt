package me.alfredobejarano.brastlewark.utils.di

import android.app.Application
import me.alfredobejarano.brastlewark.datasource.local.BrastlewarkDatabase
import me.alfredobejarano.brastlewark.datasource.local.SharedPreferencesDataSource
import me.alfredobejarano.brastlewark.datasource.network.GnomeApiService
import me.alfredobejarano.brastlewark.repository.CachedPhotoRepository
import me.alfredobejarano.brastlewark.repository.GnomeRepository
import me.alfredobejarano.brastlewark.viewmodel.GnomeDetailsViewModel
import me.alfredobejarano.brastlewark.viewmodel.GnomeListViewModel

class Injector private constructor(app: Application) {
    companion object {
        private var INSTANCE: Injector? = null

        fun getInstance(app: Application) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: createInstance(app)
        }

        private fun createInstance(app: Application) = Injector(app)
    }

    private val gnomeApiService by lazy { GnomeApiService }
    private val database by lazy { BrastlewarkDatabase.getInstance(app) }
    private val gnomeDataBaseHelper by lazy { database.getGnomeDataBaseHelper() }
    private val sharedPreferencesDataSource by lazy { SharedPreferencesDataSource(app) }
    private val cachedPhotoDataBaseHelper by lazy { database.getCachedPhotoDataBaseHelper() }

    private val gnomeRepository by lazy {
        GnomeRepository(gnomeApiService, gnomeDataBaseHelper, sharedPreferencesDataSource)
    }

    private val cachedPhotoRepository by lazy {
        CachedPhotoRepository(
            app,
            gnomeApiService,
            cachedPhotoDataBaseHelper,
            sharedPreferencesDataSource
        )
    }

    fun provideGnomeListViewModelFactory() =
        GnomeListViewModel.Factory(gnomeRepository, cachedPhotoRepository)

    fun provideGnomeDetailsViewModelFactory() =
        GnomeDetailsViewModel.Factory(gnomeRepository, cachedPhotoRepository)
}