package com.example.screen_time_record_2.UI

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class Titles {
    @Composable
    fun DatesTitle(modifier: Modifier, date: String) {

        Text(
            text = date,
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            ),
            modifier = modifier,
        )
    }

    @Composable
    fun Title(modifier: Modifier) {
        Text(
            text = "Screen Time Recorder",
            style = MaterialTheme.typography.h3.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                textAlign = TextAlign.Center
            ),
            modifier = modifier
                .padding(top = 24.dp, bottom = 8.dp)
                .wrapContentHeight()
        )
    }

}


