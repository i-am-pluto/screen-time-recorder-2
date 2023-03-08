package com.example.screen_time_record_2.services

import android.app.Activity
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.screen_time_record_2.MainActivity
import com.example.screen_time_record_2.database.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class RecordScreenTimeBackgroundService(
    appContext: Context,
    workerParams: WorkerParameters,
    private val db: ViewModel,
) : Worker(appContext, workerParams) {


    private val sharedPreferences =
        appContext.getSharedPreferences("RollNumber", Context.MODE_PRIVATE)


    private val date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDate.now()
    } else {
        TODO("VERSION.SDK_INT < O")
    }

    private val userStatsService = UserStatsService(
        date = date,
        context = appContext,
        db = db,
    )

    private val backendApiService = BackendApiService(
        context = appContext,
        sharedPreferences = sharedPreferences,
        rollNumber = this.getRollNumber(),
        url = "http://192.168.29.247:3000"
    )


    override fun doWork(): Result {
        println("here is me i am here")


        GlobalScope.launch(Dispatchers.Main) {

            println("bitch ass nigga")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                userStatsService.getStats().observeForever { stats ->

                    // if the stats wasnt saved it will be generated and updated to the current database
                    // when it is done we need to get the saved database yet from the background and then check what date is missing and provide the information from that date
                    // always send todays screen time so stats is to be sent and the rest should be calculated

                    backendApiService.getLatestUpdatedStatDate().observeForever { dateString ->

                        val date1: LocalDate = LocalDate.parse(dateString)
                        val dateTemp =
                            Date.from(date1.atStartOfDay(ZoneId.systemDefault()).toInstant())
                        db.getUserStatsAfterDate(dateTemp).observeForever { userStatsList ->
                            var sendDataList: MutableMap<String, MutableMap<String, String?>> =
                                mutableMapOf()
                            userStatsList.forEach { userStats1 ->
                                val d1 = userStats1.date.toString()
                                sendDataList[d1] = mutableMapOf(
                                    "day_use" to userStats1.day_use,
                                    "night_use" to userStats1.day_use,
                                    "unlocks" to userStats1.day_use
                                )
                            }

                            sendDataList[userStatsService.getDate().toString()] =
                                mutableMapOf(
                                    "day_use" to stats["day_use"],
                                    "night_use" to stats["night_use"],
                                    "unlocks" to stats["unlocks"]
                                )


                            val immutableDataList: Map<String, Map<String, String>> =
                                sendDataList.mapValues { innerMap ->
                                    innerMap.value.filterValues { it != null }
                                        .mapValues { it.value!! }
                                }

                            backendApiService.sendStats(immutableDataList)

                        }


                    }

                }

            }
        }


        return Result.success()
    }


    private fun getRollNumber(): String {
        return this.sharedPreferences.getString("RollNumber", "").toString();
    }


}