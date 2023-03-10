package com.example.screen_time_record_2

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.screen_time_record_2.UI.MainScreen
import com.example.screen_time_record_2.database.ViewModel
import com.example.screen_time_record_2.services.BackendApiService
import com.example.screen_time_record_2.services.RecordScreenTimeWorkerFactory
import com.example.screen_time_record_2.ui.theme.Screentimerecord2Theme
import java.time.*
import java.util.*


class MainActivity : ComponentActivity() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context: Context = applicationContext

        val db: ViewModel = ViewModelProvider(this).get(ViewModel::class.java)
        val sharedPreferences = getSharedPreferences("RollNumber", Context.MODE_PRIVATE)
        val date = LocalDate.now()


        val backendApiService = BackendApiService(
            context = context,
            sharedPreferences = sharedPreferences,
            rollNumber = sharedPreferences.getString("RollNumber", "").toString(),
            url = "https://screen-time-recorder.onrender.com/api"
        )
        val mainScreen = MainScreen(
            this, context, db, this, sharedPreferences, backendApiService = backendApiService
        )
        // schedules a job to background every 12 hours
        val workerFactory = RecordScreenTimeWorkerFactory(db, backendApiService)

        val configuration = Configuration.Builder().setWorkerFactory(workerFactory).build()


        db.readAll.observeForever {
            println(it)
        }





        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent()
            val packageName = packageName
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }

        WorkManager.initialize(this, configuration)
        workerFactory.scheduleWorker(this)







        setContent {
            Screentimerecord2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    mainScreen.ScreenTimeRecordScreen()
                }
            }
        }




    }
}






