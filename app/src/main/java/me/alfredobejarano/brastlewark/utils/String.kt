package me.alfredobejarano.brastlewark.utils

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