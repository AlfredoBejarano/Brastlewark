package me.alfredobejarano.brastlewark.utils

import java.util.Locale

private const val SEPARATOR = "|"

/**
 * Checks if the text contains the "http" protocol and replaces it with https.
 *
 * If the string is not an URL or it contains https already, no change is made.
 */
fun String.asSafeURL() = if (contains("http") && !contains("https")) {
    replace("http", "https")
} else {
    this
}

fun List<String>.asString() = StringBuilder().also { builder ->
    forEach {
        builder.append(it)
        builder.append(SEPARATOR)
    }
}.toString()

fun String.asList() = this.split(SEPARATOR).toList().filter { it.isNotEmpty() }.sortedBy { it }

fun String.getFileNameFromURL() = split("/").last()

fun List<String>.asCleanString() = this.map { it.replace(" T", "T") }.sortedBy { it }
    .toString()
    .replace("[", "")
    .replace(", ]", "")
    .replace("]", "")

fun String.getNameCapitalLetters() = if (length > 2 && contains(" ")) {
    val words = this.split(" ")
    "${words.first().first()}${words.last().first()}".toUpperCase(Locale.getDefault())
} else if (length < 2) {
    this
} else {
    this.take(2)
}