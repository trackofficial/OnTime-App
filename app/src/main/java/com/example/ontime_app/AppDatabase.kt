package com.example.ontime_app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ontime_app.model.TimelineItem

@Database(entities = [TimelineItem::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timelineDao(): TimelineDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ontime_app_db"
                )
                    .fallbackToDestructiveMigration() // заменить на миграции в проде если нужно
                    .build()
                INSTANCE = inst
                inst
            }
        }
    }
}