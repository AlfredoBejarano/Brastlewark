package me.alfredobejarano.brastlewark.datasource.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import me.alfredobejarano.brastlewark.datasource.local.CachedPhotoDataBaseHelper.CachedPhotoEntry.PATH_COLUMN
import me.alfredobejarano.brastlewark.datasource.local.CachedPhotoDataBaseHelper.CachedPhotoEntry.SQL_CREATE_TABLE
import me.alfredobejarano.brastlewark.datasource.local.CachedPhotoDataBaseHelper.CachedPhotoEntry.SQL_DELETE_TABLE
import me.alfredobejarano.brastlewark.datasource.local.CachedPhotoDataBaseHelper.CachedPhotoEntry.TABLE_NAME
import me.alfredobejarano.brastlewark.datasource.local.CachedPhotoDataBaseHelper.CachedPhotoEntry.URL_COLUMN

class CachedPhotoDataBaseHelper(private val db: SQLiteDatabase?) {
    // Gnome(id=, name=, thumbnailUrl=, age=, weight=, height=, hairColor=, professions=[], friends=[])
    private object CachedPhotoEntry : BaseColumns {
        const val TABLE_NAME = "cached_pictures"
        const val URL_COLUMN = "url"
        const val PATH_COLUMN = "path"
        const val URL_KEY = "url_param"

        const val SQL_CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
                "$URL_COLUMN TEXT PRIMARY KEY, " +
                "$PATH_COLUMN TEXT)"

        const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    fun createTable() {
        db?.execSQL(SQL_CREATE_TABLE)
    }

    fun nukeTable() {
        db?.execSQL(SQL_DELETE_TABLE)
    }

    fun create(src: String, path: String): String {
        val values = ContentValues().apply {
            put(URL_COLUMN, src)
            put(PATH_COLUMN, path)
        }
        return db?.insert(TABLE_NAME, null, values).toString()
    }

    fun getCachedPicture(src: String): String {
        val selection = "$URL_COLUMN = ?"
        val selectionArgs = arrayOf(src)
        return db?.query(
            TABLE_NAME,
            arrayOf(PATH_COLUMN),
            selection,
            selectionArgs,
            null,
            null,
            null
        )?.run {
            if (moveToNext()) {
                getString(getColumnIndex(PATH_COLUMN))
            } else {
                ""
            }
        } ?: ""
    }
}