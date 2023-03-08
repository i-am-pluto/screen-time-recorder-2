package com.example.screen_time_record_2.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.RoomDatabase
import com.example.screen_time_record_2.UserStatsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserStatsRepository
    val readAll = MutableLiveData<List<UserStats>>()

    init {
        val db = UserStatsDatabase.getDatabase(application).userDao()
        repository = UserStatsRepository(db)

        viewModelScope.launch(Dispatchers.IO) {
            readAll.postValue(repository.getAllUserStats())
        }
    }

    fun insertUserStats(userStats: UserStats) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUserStats(userStats = userStats)
        }
    }

    fun deleteUserStats(userStats: UserStats) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteUserStats(userStats = userStats)
        }
    }

    fun updateUserStats(userStats: UserStats) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserStats(userStats)
        }
    }

    fun getUserStatsByDate(date: Date): MutableLiveData<UserStats> {
        var result = MutableLiveData<UserStats>()

        viewModelScope.launch(Dispatchers.IO) {
            result.postValue(repository.getUserStatsByDate(date))
        }

        return result
    }

    fun getUserStatsAfterDate(date: Date): MutableLiveData<List<UserStats>> {

        var result = MutableLiveData<List<UserStats>>()

        viewModelScope.launch(Dispatchers.IO) {
            result.postValue(repository.getAllUserStatsAfterDate(date))
        }

        return result
    }


}