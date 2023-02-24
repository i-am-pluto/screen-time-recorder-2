package com.example.screen_time_record_2.UI

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.screen_time_record_2.database.ViewModel
import com.example.screen_time_record_2.services.UserStatsService
import java.time.LocalDate

class MainScreen(
    private val activity: Activity,
    private val context: Context,
    private val db: ViewModel
) {


    private val titles = Titles()
    private val textFields = TextFields(activity = activity)
    private val cards = Cards()


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun ScreenTimeRecordScreen() {


        var date by remember {
            mutableStateOf(LocalDate.now())
        }

        var rollNumber by rememberSaveable {
            mutableStateOf("")
        }

        val userStatsService =
            UserStatsService(date, context = context, db = this.db, activity = activity)
        val stats = userStatsService.getStats()
        userStatsService.saveStats()


        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            titles.Title(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 40.dp)
                    .fillMaxWidth()
            )


            textFields.EnterRollNumber(
                rollNumber = rollNumber, onRollNumberChange = { rollNumber = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp, horizontal = 30.dp),
            )

            cards.MainCard(date = date, stats = stats, onDateChange = { date = it })

        }
    }


}