package com.example.screen_time_record_2.UI

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.screen_time_record_2.R
import java.time.LocalDate

class Cards {


    @Composable
    fun MainCard(date: LocalDate, stats: Map<String, String>, onDateChange: (LocalDate) -> Unit) {
        Card(
            elevation = 10.dp,
            modifier = Modifier.padding(horizontal = 30.dp, vertical = 40.dp)
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.CurrentTimeCard(
                    onDateChange = onDateChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    date = date, stats = stats,

                    )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun CurrentTimeCard(
        modifier: Modifier,
        stats: Map<String, String>,
        date: LocalDate,
        onDateChange: (LocalDate) -> Unit
    ) {
        val paddingModifierText = Modifier.padding(horizontal = 10.dp)


        var usageTimeDuringDay = stats["day_use"]
        var usageTimeDuringNight = stats["night_use"]
        var unlockCounts = stats["unlocks"]


        val leftTextStyle = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
        )

        val Titles: Titles = Titles()


        Column(verticalArrangement = Arrangement.Center, modifier = modifier) {


            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    onClick = {
                        onDateChange(date.minusDays(1))
                    },
                    modifier = Modifier
                        .width(50.dp)
                        .height(35.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = null
                    )
                }

                Titles.DatesTitle(
                    modifier = Modifier
                        .weight(4f)
                        .height(25.dp), date.toString()
                )


                Button(
                    onClick = {
                        if (date != LocalDate.now())
                            onDateChange(date.plusDays(1))
                    },
                    modifier = Modifier
                        .width(50.dp)
                        .height(35.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                        contentDescription = null
                    )
                }

            }


            Row(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "Screen Time (Day)",
                    modifier = Modifier
                        .weight(3f)
                        .padding(4.dp), style = leftTextStyle

                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = ":",
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp), textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = usageTimeDuringDay.toString(),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)

                )
            }
            Row(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "Screen Time (Night)",
                    modifier = Modifier
                        .weight(3f)
                        .padding(4.dp), style = leftTextStyle

                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = ":",
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp), textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = usageTimeDuringNight.toString(),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)

                )
            }
            Row(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "Unlock Counts",
                    modifier = Modifier
                        .weight(3f)
                        .padding(4.dp), style = leftTextStyle

                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = ":",
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp), textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unlockCounts.toString(),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)

                )
            }
        }
    }

}