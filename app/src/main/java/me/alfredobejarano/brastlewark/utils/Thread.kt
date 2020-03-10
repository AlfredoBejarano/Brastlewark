package me.alfredobejarano.brastlewark.utils

import java.util.concurrent.Executors

/**
 * Executor that gives access to a worker thread to run code on.
 */
private val SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor()

/**
 * Executes a given block of code in a given [SINGLE_THREAD_EXECUTOR].
 * @param block Block of code to run in a worker thread.
 */
fun runOnWorkerThread(block: () -> Unit) = SINGLE_THREAD_EXECUTOR.execute(block)