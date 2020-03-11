package me.alfredobejarano.brastlewark.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

fun <T> ViewModel.forLiveData(block: () -> T) = MutableLiveData<T>().apply {
    runOnWorkerThread {
        postValue(block())
    }
} as LiveData<T>

fun <T> MutableLiveData<T>.map(block: () -> T) = runOnWorkerThread {
    postValue(block())
}