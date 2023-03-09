package com.example.screen_time_record_2.services

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.*
import com.example.screen_time_record_2.database.ViewModel
import java.time.Duration
import java.util.concurrent.TimeUnit

class RecordScreenTimeWorkerFactory(private val db: ViewModel,private val backendApiService: BackendApiService) : WorkerFactory() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleWorker(ctx: Context) {
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()

        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            RecordScreenTimeBackgroundService::class.java,
            Duration.ofMinutes(15)
        ).setConstraints(constraints).build()

        WorkManager.getInstance(ctx).enqueue(periodicWorkRequest)
    }


    override fun createWorker(
        appContext: Context, workerClassName: String, workerParameters: WorkerParameters
    ): ListenableWorker? {
        return RecordScreenTimeBackgroundService(appContext, workerParameters, db = this.db, backendApiService = backendApiService)
    }
}