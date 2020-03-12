package me.alfredobejarano.brastlewark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object Events {
    private val errorMutableLiveData = MutableLiveData<Exception>()
    val errorLiveData = errorMutableLiveData as LiveData<Exception>

    fun reportException(e: Exception?) = errorMutableLiveData.postValue(e)
}