package me.alfredobejarano.brastlewark.utils

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

fun String.asList() = this.split(SEPARATOR).toList()

fun String.getFileNameFromURL() = split("/").last()