package me.alfredobejarano.brastlewark.datasource.local

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BrastlewarkDatabase private constructor(ctx: Context) :
    SQLiteOpenHelper(ctx, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "brastlewark.db"
        private const val DATABASE_VERSION = 1

        private var INSTANCE: BrastlewarkDatabase? = null

        fun getInstance(app: Application): BrastlewarkDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: createInstance(app).also { INSTANCE = it }
        }

        private fun createInstance(app: Application): BrastlewarkDatabase {
            val database = BrastlewarkDatabase(app)
            val writableDataBase = database.writableDatabase
            return database.apply {
                gnomeDataBaseHelper = GnomeDataBaseHelper(writableDataBase)
                cachedPhotoDataBaseHelper = CachedPhotoDataBaseHelper(writableDataBase)
            }
        }
    }

    private lateinit var gnomeDataBaseHelper: GnomeDataBaseHelper
    private lateinit var cachedPhotoDataBaseHelper: CachedPhotoDataBaseHelper

    override fun onCreate(db: SQLiteDatabase?) {
        gnomeDataBaseHelper = GnomeDataBaseHelper(db).also { it.createTable() }
        cachedPhotoDataBaseHelper = CachedPhotoDataBaseHelper(db).also { it.createTable() }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        GnomeDataBaseHelper(db).nukeTable()
        CachedPhotoDataBaseHelper(db).nukeTable()
        onCreate(db)
    }

    fun getGnomeDataBaseHelper() = gnomeDataBaseHelper
    fun getCachedPhotoDataBaseHelper() = cachedPhotoDataBaseHelper
}