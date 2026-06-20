package com.swx.dongzhou.HistoryDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(version = 1, entities = [History::class], exportSchema = false)
@TypeConverters(HistoryConverters::class)
abstract class HistoryDatabase: RoomDatabase() {

    abstract fun HistoryDao(): HistoryDao

    companion object{
        private var instance: HistoryDatabase?=null

        fun getDatabase(context: Context): HistoryDatabase{
            instance?.let { return it }
            return  Room.databaseBuilder(context.applicationContext,
                HistoryDatabase::class.java, "history_database")
                .build().apply {
                    instance = this
                }
        }

    }
}
