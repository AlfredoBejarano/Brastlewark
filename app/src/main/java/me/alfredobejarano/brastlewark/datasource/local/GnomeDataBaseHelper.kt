package me.alfredobejarano.brastlewark.datasource.local

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import me.alfredobejarano.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.AGE_COLUMN
import me.alfredobejarano.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.FRIENDS_COLUMN
import me.alfredobejarano.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.HAIR_COLOR_COLUMN
import me.alfredobejarano.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.HEIGHT_COLUMN
import me.alfredobejarano.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.NAME_COLUMN
import me.alfredobejarano.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.PROFESSIONS_COLUMN
import me.alfredobejarano.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.SQL_CREATE_TABLE
import me.alfredobejarano.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.SQL_DELETE_TABLE
import me.alfredobejarano.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.TABLE_NAME
import me.alfredobejarano.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.THUMBNAIL_URL_COLUMN
import me.alfredobejarano.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.WEIGHT_COLUMN
import me.alfredobejarano.brastlewark.model.Gnome

class GnomeDataBaseHelper(private val db: SQLiteDatabase?) {
    private object GnomeEntry : BaseColumns {
        const val TABLE_NAME = "gnomes"
        const val NAME_COLUMN = "name"
        const val THUMBNAIL_URL_COLUMN = "thumbnail_url"
        const val AGE_COLUMN = "age"
        const val WEIGHT_COLUMN = "weight"
        const val HEIGHT_COLUMN = "height"
        const val HAIR_COLOR_COLUMN = "hair_color"
        const val PROFESSIONS_COLUMN = "professions"
        const val FRIENDS_COLUMN = "friends"
        const val NAME_KEY = "name_param"

        const val SQL_CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY" +
                "$NAME_COLUMN TEXT" +
                "$THUMBNAIL_URL_COLUMN TEXT" +
                "$AGE_COLUMN INTEGER" +
                "$WEIGHT_COLUMN REAL" +
                "$HEIGHT_COLUMN REAL" +
                "$HAIR_COLOR_COLUMN TEXT" +
                "$PROFESSIONS_COLUMN TEXT" +
                "$FRIENDS_COLUMN TEXT)"

        const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    fun createTable() {
        db?.execSQL(SQL_CREATE_TABLE)
    }

    fun nukeTable() {
        db?.execSQL(SQL_DELETE_TABLE)
    }

    fun create(gnome: Gnome) {
        val values = ContentValues().apply {
            gnome.run {
                put(BaseColumns._ID, id)
                put(NAME_COLUMN, name)
                put(THUMBNAIL_URL_COLUMN, thumbnailUrl)
                put(AGE_COLUMN, age)
                put(WEIGHT_COLUMN, weight)
                put(HEIGHT_COLUMN, height)
                put(HAIR_COLOR_COLUMN, hairColor)
                put(PROFESSIONS_COLUMN, professions.toString())
                put(FRIENDS_COLUMN, friends.toString())
            }
        }
        db?.insert(TABLE_NAME, null, values)?.toInt() ?: -1
    }

    private fun getGnomeFromCursor(cursor: Cursor?) = cursor?.run {
        val id = getInt(getColumnIndex(BaseColumns._ID))
        val name = getString(getColumnIndex(NAME_COLUMN))
        val thumbnail = getString(getColumnIndex(THUMBNAIL_URL_COLUMN))
        val age = getInt(getColumnIndex(AGE_COLUMN))
        val weight = getDouble(getColumnIndex(WEIGHT_COLUMN))
        val height = getDouble(getColumnIndex(HEIGHT_COLUMN))
        val hairColor = getString(getColumnIndex(HAIR_COLOR_COLUMN))
        val professions = listOf(getString(getColumnIndex(PROFESSIONS_COLUMN)))
        val friends = listOf(getString(getColumnIndex(FRIENDS_COLUMN)))

        Gnome(id, name, thumbnail, age, weight, height, hairColor, professions, friends).also {
            close()
        }
    } ?: Gnome()

    fun getAllGnomes(): List<Gnome> {
        val cursor = db?.query(TABLE_NAME, null, null, null, null, null, null)
        return mutableListOf<Gnome>().run {
            while (cursor?.moveToNext() == true) {
                add(getGnomeFromCursor(cursor))
            }
            filter { it.id > -1 }
        }
    }

    fun getGnomeByName(gnomeName: String): Gnome {
        val selection = "$NAME_COLUMN = ?"
        val args = arrayOf(gnomeName)
        val cursor = db?.query(TABLE_NAME, null, selection, args, null, null, null)
        return getGnomeFromCursor(cursor)
    }
}