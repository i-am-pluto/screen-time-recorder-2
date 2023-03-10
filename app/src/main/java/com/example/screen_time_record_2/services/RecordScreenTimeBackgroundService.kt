package com.example.screen_time_record_2.services

import android.app.Activity
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class RecordScreenTimeBackgroundService(
    appContext: Context,
    workerParams: WorkerParameters,
    private val db: ViewModel,
    private val backendApiService: BackendApiService
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




    override fun doWork(): Result {
        println("here is me i am here")

        Log.d("check","initiating worker");

        GlobalScope.launch(Dispatchers.Main) {

            Log.d("check","schedule");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                userStatsService.getStats().observeForever { stats ->

                    // if the stats wasnt saved it will be generated and updated to the current database
                    // when it is done we need to get the saved database yet from the background and then check what date is missing and provide the information from that date
                    // always send todays screen time so stats is to be sent and the rest should be calculated

                    backendApiService.getLatestUpdatedStatDate().observeForever { dateString ->


                        val formatter =
                            DateTimeFormatter.ofPattern("yyyy-MM-dd") // create a formatter for the input date string
                        val date1 = LocalDate.parse(
                            dateString,
                            formatter
                        ) // parse the date string to a LocalDate object                        val date1 = LocalDate.parse(cleanedDateString, formatter)
                        val dateTemp =
                            Date.from(date1.atStartOfDay(ZoneId.systemDefault()).toInstant())
                        db.getUserStatsAfterDate(dateTemp).observeForever { userStatsList ->
                            var sendDataList: MutableMap<String, MutableMap<String, String?>> =
                                mutableMapOf()
                            userStatsList.forEach { userStats1 ->
                                val d1 = userStats1.date

                                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                                val formattedDate = dateFormat.format(d1)

                                sendDataList[formattedDate] = mutableMapOf(
                                    "day_use" to userStats1.day_use,
                                    "night_use" to userStats1.night_use,
                                    "unlocks" to userStats1.unlocks
                                )
                            }

                            sendDataList[userStatsService.getDate().toString()] =
                                mutableMapOf(
                                    "day_use" to stats["day_use"],
                                    "night_use" to stats["night_use"],
                                    "unlocks" to stats["unlocks"]
                                )

                            println(sendDataList)


                            val immutableDataList: Map<String, Map<String, String>> =
                                sendDataList.mapValues { innerMap ->
                                    innerMap.value.filterValues { it != null }
                                        .mapValues { it.value!! }
                                }

                            backendApiService.sendStats(immutableDataList)
                            println("parikshit is here")
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