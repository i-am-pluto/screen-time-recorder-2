package com.example.screen_time_record_2

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.screen_time_record_2.UI.MainScreen
import com.example.screen_time_record_2.database.ViewModel
import com.example.screen_time_record_2.ui.theme.Screentimerecord2Theme
import java.time.*
import java.util.*


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context: Context = applicationContext

        val db : ViewModel = ViewModelProvider(this).get(ViewModel::class.java)

        // Use the ViewModel to perform database operations

        val date = LocalDate.now()

        val mainScreen = MainScreen(this,context,db)

        setContent {
            Screentimerecord2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    mainScreen.ScreenTimeRecordScreen()
                }
            }
        }
    }
}






