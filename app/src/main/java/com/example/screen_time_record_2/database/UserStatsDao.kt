package com.example.screen_time_record_2.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.Date
import java.time.LocalDate

@Dao
interface UserStatsDao {
    @Insert
    fun insert(userStats: UserStats)

    @Delete
    fun delete(user: UserStats)

    @Query("SELECT * FROM userstats")
    fun getAll(): List<UserStats>

    @Query("SELECT * FROM userstats WHERE userstats.date > :date")
    fun getAllAfterDate(date: Date) : List<UserStats>

    @Query("SELECT * FROM userstats WHERE userstats.date = :date")
    fun getByDate(date: Date) : UserStats

    @Update
    fun updateUserStats(userStats: UserStats)

}