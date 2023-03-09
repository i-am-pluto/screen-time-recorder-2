package com.example.screen_time_record_2.services

import android.app.Activity
import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.screen_time_record_2.MainActivity
import com.example.screen_time_record_2.database.UserStats
import com.example.screen_time_record_2.database.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

class UserStatsService(
    private val date: LocalDate,
    private val context: Context,
    private val db: ViewModel,
) {

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.generateStats()
        }
    }

    private var stats = MutableLiveData<Map<String, String>>();
    fun getStats(): MutableLiveData<Map<String, String>> {
        return stats
    }

    fun getDate(): LocalDate {
        return this.date;
    }

    fun saveStats() {
        GlobalScope.launch(Dispatchers.Main) {
            stats.observeForever { stats ->

                val date: Date? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()) as Date
                } else {
                    TODO("VERSION.SDK_INT < O")
                }

                println("here")


                val userStats = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    date?.let {
                        UserStats(
                            date = it,
                            day_use = stats["day_use"].toString(),
                            night_use = stats["night_use"].toString(),
                            unlocks = stats["unlocks"].toString(),
                            id = 0
                        )
                    }
                } else {
                    TODO("VERSION.SDK_INT < O")
                }


                date?.let { date ->
                    db.getUserStatsByDate(date)
                        .observeForever { userStats1 ->
                            println(userStats)
                            if (userStats1?.let { db.getUserStatsByDate(date) } == null) {
                                println("here adding new")
                                userStats?.let { userStats2 ->
                                    db.insertUserStats(userStats2)
                                }
                            } else userStats?.let { userStats3 ->
                                println("here updating")
                                db.updateUserStats(userStats3)
                            }
                        }
                }
            }
        }


    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun generateStats() {

        val date: String = this.date.toString()

        val appOps: AppOpsManager =
            context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

        val mode = appOps.unsafeCheckOp(
            "android:get_usage_stats",
            Process.myUid(), context.packageName
        )
        val granted = mode == AppOpsManager.MODE_ALLOWED

        if (!granted) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            context.startActivity(intent);
        }

        val localDate: LocalDate = LocalDate.parse(date.toString());

        val dateTemp = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        GlobalScope.launch(Dispatchers.Main) {
            val tmp: Unit = db.getUserStatsByDate(dateTemp)
                .observeForever { userStats ->
                    if (userStats != null && localDate != LocalDate.now()) {
//                load from database
                        stats.postValue(
                            mapOf<String, String>(
                                "day_use" to userStats.day_use,
                                "night_use" to userStats.night_use,
                                "unlocks" to userStats.unlocks
                            )
                        )
                    } else {
                        val zoneId = ZoneId.of("Asia/Kolkata")

                        val startOfNight1 =
                            localDate.atTime(LocalTime.MIDNIGHT).atZone(zoneId).toInstant()
                                .toEpochMilli()
                        val endOfNight1 =
                            localDate.atTime(LocalTime.of(7, 0)).atZone(zoneId).toInstant()
                                .toEpochMilli()

                        val usageStatsManager =
                            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

                        val statsNight1 = usageStatsManager.queryEvents(
                            startOfNight1,
                            endOfNight1
                        )

                        val startOfNight2 =
                            localDate.atTime(LocalTime.of(19, 0)).atZone(zoneId).toInstant()
                                .toEpochMilli()
                        val endOfNight2 =
                            localDate.atTime(LocalTime.of(23, 59, 59)).atZone(zoneId).toInstant()
                                .toEpochMilli()

                        val statsNight2 = usageStatsManager.queryEvents(
                            startOfNight2,
                            endOfNight2
                        )

                        val totalNightTime: Long =
                            (calculateScreenTime(statsNight1, startOfNight1, endOfNight1)
                                ?: 0) + (calculateScreenTime(
                                statsNight2,
                                startOfNight2,
                                endOfNight2
                            )
                                ?: 0)


                        val startOfDay =
                            localDate.atTime(LocalTime.of(7, 0)).atZone(zoneId).toInstant()
                                .toEpochMilli()
                        val endOfDay =
                            localDate.atTime(LocalTime.of(19, 0)).atZone(zoneId).toInstant()
                                .toEpochMilli()

                        val dayStats = usageStatsManager.queryEvents(
                            startOfDay,
                            endOfDay
                        );
                        val totalDayTime: Long =
                            calculateScreenTime(dayStats, startOfDay, endOfDay) ?: 0

                        val nightHours = totalNightTime / (1000 * 60 * 60)
                        val nightMinutes = (totalNightTime % (1000 * 60 * 60)) / (1000 * 60)
                        val nightDurationTime = "%d:%02d".format(nightHours, nightMinutes)

                        val dayHours = totalDayTime / (1000 * 60 * 60)
                        val dayMinutes = (totalDayTime % (1000 * 60 * 60)) / (1000 * 60)
                        val dayDurationTime = "%d:%02d".format(dayHours, dayMinutes)

                        val start_time = startOfNight1
                        val end_time = endOfNight2

                        val usageEvents = usageStatsManager.queryEvents(start_time, end_time);
                        var unlockCount = 0

                        while (usageEvents.hasNextEvent()) {
                            val currentEvent = UsageEvents.Event()
                            usageEvents.getNextEvent(currentEvent)
                            if (currentEvent.eventType == UsageEvents.Event.KEYGUARD_HIDDEN) {
                                unlockCount++
                            }
                        }



                        stats.postValue(
                            mapOf<String, String>(
                                "day_use" to dayDurationTime,
                                "night_use" to nightDurationTime,
                                "unlocks" to unlockCount.toString()
                            )
                        )

                        saveStats()
                    }
                }
        }

    }

    private fun calculateScreenTime(events: UsageEvents, usageStart: Long, usageEnd: Long): Long {
        var totalTime: Long = 0
        var lastInteractive: Long = -1
        while (events.hasNextEvent()) {
            val currentEvent = UsageEvents.Event()
            events.getNextEvent(currentEvent)
            if (currentEvent.eventType == UsageEvents.Event.SCREEN_INTERACTIVE) {
                lastInteractive = currentEvent.timeStamp
            }
            if ((currentEvent.eventType == UsageEvents.Event.SCREEN_NON_INTERACTIVE ||
                        currentEvent.eventType == UsageEvents.Event.DEVICE_SHUTDOWN) &&
                lastInteractive != -1L && currentEvent.timeStamp <= usageEnd
            ) {
                val timeDiff = currentEvent.timeStamp - lastInteractive
                if (lastInteractive >= usageStart) {
                    totalTime += timeDiff
                } else if (currentEvent.timeStamp >= usageStart) {
                    totalTime += currentEvent.timeStamp - usageStart
                }
                lastInteractive = -1
            }
        }
        return totalTime
    }
}


