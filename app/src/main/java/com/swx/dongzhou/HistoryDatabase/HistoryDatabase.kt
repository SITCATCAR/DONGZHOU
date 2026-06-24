package com.swx.dongzhou.HistoryDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(version = 2, entities = [History::class], exportSchema = false)
@TypeConverters(HistoryConverters::class)
abstract class HistoryDatabase: RoomDatabase() {

    abstract fun HistoryDao(): HistoryDao

    companion object{
        private var instance: HistoryDatabase?=null

        fun getDatabase(context: Context): HistoryDatabase{
            instance?.let { return it }
            return  Room.databaseBuilder(context.applicationContext,
                HistoryDatabase::class.java, "history_database")
                .addMigrations(MIGRATION_1_2)
                .build().apply {
                    instance = this
                }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val columns = getHistoryColumns(db)
                val now = System.currentTimeMillis()

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS history_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        content TEXT NOT NULL,
                        type TEXT NOT NULL,
                        isFavorite INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        favoriteAt INTEGER
                    )
                    """.trimIndent()
                )
                if (columns.isNotEmpty()) {
                    db.execSQL(
                        """
                        INSERT INTO history_new (id, title, content, type, isFavorite, createdAt, favoriteAt)
                        SELECT
                            ${copyNullableColumn(columns, "id", "NULL")},
                            ${copyRequiredColumn(columns, "title", "''")},
                            ${copyRequiredColumn(columns, "content", "''")},
                            ${copyRequiredColumn(columns, "type", "'Text'")},
                            ${copyRequiredColumn(columns, "isFavorite", "0")},
                            ${copyRequiredColumn(columns, "createdAt", now.toString())},
                            ${copyNullableColumn(columns, "favoriteAt", "NULL")}
                        FROM history
                        """.trimIndent()
                    )
                }
                db.execSQL("DROP TABLE IF EXISTS history")
                db.execSQL("ALTER TABLE history_new RENAME TO history")
            }
        }

        private fun getHistoryColumns(database: SupportSQLiteDatabase): Set<String> {
            val columns = mutableSetOf<String>()
            database.query("PRAGMA table_info(history)").use { cursor ->
                val nameIndex = cursor.getColumnIndex("name")
                while (cursor.moveToNext()) {
                    columns.add(cursor.getString(nameIndex))
                }
            }
            return columns
        }

        private fun copyRequiredColumn(columns: Set<String>, name: String, fallback: String): String {
            return if (columns.contains(name)) {
                "COALESCE(`$name`, $fallback)"
            } else {
                fallback
            }
        }

        private fun copyNullableColumn(columns: Set<String>, name: String, fallback: String): String {
            return if (columns.contains(name)) {
                "`$name`"
            } else {
                fallback
            }
        }
    }
}
