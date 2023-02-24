package com.example.screen_time_record_2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.screen_time_record_2.database.Converters
import com.example.screen_time_record_2.database.UserStats
import com.example.screen_time_record_2.database.UserStatsDao

@Database(entities = [UserStats::class], version = 1)
@TypeConverters(Converters::class)
abstract class UserStatsDatabase : RoomDatabase() {
    abstract fun userDao(): UserStatsDao

    companion object {
        @Volatile
        private var INSTANCE: UserStatsDatabase? = null

        fun getDatabase(context: Context): UserStatsDatabase {
            // only one thread of execution at a time can enter this block of code
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserStatsDatabase::class.java,
                        "userstats"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}
