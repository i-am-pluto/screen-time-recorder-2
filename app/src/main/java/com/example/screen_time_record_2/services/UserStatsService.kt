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
import com.example.screen_time_record_2.database.UserStats
import com.example.screen_time_record_2.database.ViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

class UserStatsService(
    private val date: LocalDate,
    private val context: Context,
    private val db: ViewModel,
    private val activity: Activity
) {


    private var stats: Map<String, String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        this.generateStats()
    } else {
        TODO("VERSION.SDK_INT < Q")
    };

    fun getStats(): Map<String, String> {
        return stats;
    }


    fun saveStats() {

        val date: Date? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Date.from(this.date.atStartOfDay(ZoneId.systemDefault()).toInstant()) as Date
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        val userStats = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            date?.let {
                UserStats(
                    date = it,
                    day_use = stats["Day"].toString(),
                    night_use = stats["Night"].toString(),
                    unlocks = stats["Unlocks"].toString(),
                    id = 0
                )
            }
        } else {
            TODO("VERSION.SDK_INT < O")
        }


        if (date?.let { db.getUserStatsByDate(date) } == null) {
            userStats?.let { db.insertUserStats(it) }
        } else userStats?.let { db.updateUserStats(it) }

    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun generateStats(): Map<String, String> {

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

        val date_temp = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val tmp: UserStats? = this.db.getUserStatsByDate(date_temp).value

        if (tmp != null) {

            return mapOf<String, String>(
                "Day" to tmp.day_use,
                "Night" to tmp.night_use,
                "Unlocks" to tmp.unlocks
            )

        }

        val zoneId = ZoneId.of("Asia/Kolkata")

        val startOfNight1 =
            localDate.atTime(LocalTime.MIDNIGHT).atZone(zoneId).toInstant().toEpochMilli()
        val endOfNight1 =
            localDate.atTime(LocalTime.of(7, 0)).atZone(zoneId).toInstant().toEpochMilli()

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val statsNight1 = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            startOfNight1,
            endOfNight1
        )

        val startOfNight2 =
            localDate.atTime(LocalTime.of(19, 0)).atZone(zoneId).toInstant().toEpochMilli()
        val endOfNight2 =
            localDate.atTime(LocalTime.of(23, 59, 59)).atZone(zoneId).toInstant().toEpochMilli()

        val statsNight2 = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            startOfNight2,
            endOfNight2
        )

        var totalNightTime: Long = 0

        statsNight1.forEach { totalNightTime += it.totalTimeInForeground }
        statsNight2.forEach { totalNightTime += it.totalTimeInForeground }

        val startOfDay =
            localDate.atTime(LocalTime.of(7, 0)).atZone(zoneId).toInstant().toEpochMilli()
        val endOfDay =
            localDate.atTime(LocalTime.of(19, 0)).atZone(zoneId).toInstant().toEpochMilli()

        val dayStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            startOfDay,
            endOfDay
        );
        var totalDayTime: Long = 0

        dayStats.forEach { totalDayTime += it.totalTimeInForeground }

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

        return mapOf<String, String>(
            "Day" to dayDurationTime,
            "Night" to nightDurationTime,
            "Unlocks" to unlockCount.toString()
        )

    }

}