package com.example.screen_time_record_2.database

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.*

class UserStatsRepository(private val userStatsDao: UserStatsDao) {

    suspend fun insertUserStats(userStats: UserStats) {
        userStatsDao.insert(userStats)
    }

    suspend fun deleteUserStats(userStats: UserStats) {
        userStatsDao.delete(userStats)
    }

    fun getAllUserStats(): List<UserStats> {
        return userStatsDao.getAll()
    }

    suspend fun getUserStatsByDate(date: Date): UserStats {
        return userStatsDao.getByDate(date)
    }

    suspend fun getAllUserStatsAfterDate(date: Date): List<UserStats> {
        return userStatsDao.getAllAfterDate(date)
    }

    suspend fun updateUserStats(userStats: UserStats) {
        userStatsDao.updateUserStats(userStats = userStats)
    }

}