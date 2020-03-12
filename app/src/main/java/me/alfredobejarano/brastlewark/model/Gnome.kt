package me.alfredobejarano.brastlewark.model

data class Gnome(
    val id: Int = -1,
    val name: String = "",
    val thumbnailUrl: String = "",
    val age: Int = 0,
    val weight: Int = 0,
    val height: Int = 0,
    val hairColor: String = "",
    val professions: List<String> = emptyList(),
    val friends: List<String> = emptyList()
)