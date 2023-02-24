package com.example.screen_time_record_2.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDate
import java.util.Date

@Entity(tableName = "userstats")
data class UserStats(

    @PrimaryKey(autoGenerate = true)
    var id:Int,

    @ColumnInfo(name = "day_use")
    var day_use: String,

    @ColumnInfo(name = "night_use")
    var night_use: String,

    @ColumnInfo(name = "unlocks")
    var unlocks: String,

    @ColumnInfo(name = "date")
    var date: Date

)
