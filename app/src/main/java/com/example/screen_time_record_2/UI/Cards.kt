package com.example.screen_time_record_2.UI

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.screen_time_record_2.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Cards {


    @Composable
    fun MainCard(
        date: LocalDate,
        stats: Map<String, String>,
        onDateChange: (LocalDate) -> Unit,
        modifier: Modifier
    ) {
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
        val leftTextStyle = MaterialTheme.typography.h6.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
        )
        val rightTextStyle = MaterialTheme.typography.h6.copy(
            fontWeight = FontWeight.Bold,
        )

        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface, MaterialTheme.shapes.medium)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                content = {
                    IconButton(
                        onClick = { onDateChange(date.minusDays(1)) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = null,
                            tint = MaterialTheme.colors.onSurface
                        )
                    }
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h5.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = MaterialTheme.colors.onSurface
                        )
                    )
                    IconButton(
                        onClick = { if (date != LocalDate.now()) onDateChange(date.plusDays(1)) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                            contentDescription = null,
                            tint = MaterialTheme.colors.onSurface
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            stats.forEach { (title, value) ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = title,
                        modifier = Modifier.weight(3f),
                        style = leftTextStyle,
                        color = MaterialTheme.colors.onSurface
                    )
                    Text(
                        text = ":",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.onSurface
                    )
                    Text(
                        text = value,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colors.primary
                    )
                }
            }
        }
    }


}