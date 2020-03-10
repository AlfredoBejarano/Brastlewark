package me.alfredobejarano.brastlewark.datasource.network

import me.alfredobejarano.brastlewark.model.Gnome
import me.alfredobejarano.brastlewark.utils.runOnWorkerThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * Kotlin object that handles all the URL connections through the project.
 */
object NetworkAdapter {

    fun getResource(
        resourceURL: String,
        timeoutSeconds: Long,
        onSuccess: (gnomes: List<Gnome>) -> Unit,
        onError: (e: Exception) -> Unit
    ) = try {
        runOnWorkerThread {
            val url = URL(resourceURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection

            TimeUnit.SECONDS.toMillis(timeoutSeconds).toInt().run {
                httpURLConnection.readTimeout = this
                httpURLConnection.connectTimeout = this
            }

            val jsonString = getResourceAsJsonString(httpURLConnection)
            val population = getPopulationFromJson(jsonString)
            val gnomes = getGnomes(population)

            onSuccess(gnomes)
        }
    } catch (e: Exception) {
        onError(e)
    }

    /**
     * Retrieves the remote resource JSON as a String.
     * @param httpURLConnection
     */
    private fun getResourceAsJsonString(httpURLConnection: HttpURLConnection) =
        BufferedReader(InputStreamReader(httpURLConnection.inputStream)).run {
            StringBuilder().apply { forEachLine { append(it) } }.toString().also { close() }
        }

    /**
     * Retrieves the population of Gnomes from the population JSON string.
     * @param json String containing the JSON data for the population.
     */
    private fun getPopulationFromJson(json: String) = JSONObject(json).getJSONArray("Brastlewark")

    /**
     * Retrieves the list of Gnome population form the JSONArray.
     * @param jsonArray JSONArray to fetch the Gnome population.
     */
    private fun getGnomes(jsonArray: JSONArray) = jsonArray.run {
        mutableListOf<Gnome>().apply {
            for (i in 0 until length()) {
                add(getGnome(getJSONObject(i)))
            }
        } as List<Gnome>
    }

    /**
     * Retrieves a Gnome from a JSONObject.
     * @param jsonObject JSONObject to retrieve the Gnome from.
     */
    private fun getGnome(jsonObject: JSONObject) = jsonObject.run {
        Gnome(
            id = getInt("id"),
            name = getString("name"),
            thumbnailUrl = getString("thumbnail"),
            age = getInt("age"),
            weight = getDouble("weight"),
            height = getDouble("height"),
            hairColor = getString("hair_color"),
            professions = getJSONArray("professions").asStringList(),
            friends = getJSONArray("friends").asStringList()
        )
    }

    /**
     * Parses a JSONArray as a List of Strings.
     */
    private fun JSONArray.asStringList() = this.let { jsonArray ->
        mutableListOf<String>().apply {
            for (i in 0 until jsonArray.length()) {
                add(jsonArray.get(i).toString())
            }
        } as List<String>
    }
}