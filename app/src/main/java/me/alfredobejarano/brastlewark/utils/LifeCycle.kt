package me.alfredobejarano.brastlewark.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import me.alfredobejarano.brastlewark.Events

fun <T> LiveData<Pair<T?, Exception?>>.observeWith(
    owner: LifecycleOwner,
    onComplete: (result: T) -> Unit
) = this.observe(owner, Observer {
    it?.first?.let(onComplete) ?: run {
        Events.reportException(it.second)
    }
})

fun <T> LiveData<Pair<T?, Exception?>>.observeWith(
    owner: LifecycleOwner,
    onComplete: (result: T) -> Unit,
    onError: (e: Exception?) -> Unit
) = this.observe(owner, Observer {
    it?.first?.let(onComplete) ?: run {
        onError(it.second)
    }
})