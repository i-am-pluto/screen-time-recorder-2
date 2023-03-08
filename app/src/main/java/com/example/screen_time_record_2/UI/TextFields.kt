package com.example.screen_time_record_2.UI

import android.app.Activity
import android.app.AlertDialog
import android.content.SharedPreferences
import android.text.InputType
import android.widget.EditText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.screen_time_record_2.R


class TextFields(private val activity: Activity) {

    @Composable
    fun EnterRollNumber(
        modifier: Modifier,
        rollNumber: String,
        onRollNumberChange: (String) -> Unit,
        sharedPreferences: SharedPreferences
    ) {

        if (rollNumber == "") {
            enterRollNumberAlert(
                rollNumber = rollNumber,
                onRollNumberChange = onRollNumberChange,
                sharedPreferences = sharedPreferences
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = CenterVertically,
            modifier = modifier
        ) {

            TextField(
                value = rollNumber,
                onValueChange = onRollNumberChange, label = { Text(text = "NSUT ROLL NUMBER") },
                modifier = Modifier
                    .weight(4f)
                    .padding(10.dp), enabled = false
            )
            Button(
                onClick = {
                    enterRollNumberAlert(
                        rollNumber = rollNumber,
                        onRollNumberChange = onRollNumberChange,
                        sharedPreferences = sharedPreferences
                    )
                },
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_edit_24),
                    contentDescription = null
                )
            }
        }

    }


    private fun enterRollNumberAlert(
        rollNumber: String,
        onRollNumberChange: (String) -> Unit,
        error: Boolean = false,
        sharedPreferences: SharedPreferences
    ) {
        var builder = AlertDialog.Builder(activity)
        builder.setTitle("Nsut Roll Number")
        builder.setIcon(R.drawable.baseline_edit_24)
        builder.setMessage("Must enter NSUT Roll Number for the application to work!!")

        val input = EditText(activity)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        if (error) {
            input.error = "Enter Correct RollNumber Only ex -- 2020UCA1891"
        }
        builder.setPositiveButton(
            android.R.string.ok
        ) { dialog, which ->
            var temp = input.text.toString()
            if (temp.matches(Regex("[0-9]{4}[A-Za-z]{3}[0-9]{4}"))) {
                onRollNumberChange(temp)
                var editor = sharedPreferences.edit()
                editor.putString("RollNumber", temp)
                editor.commit();
            } else {
                dialog.dismiss()
                enterRollNumberAlert(
                    rollNumber = rollNumber,
                    onRollNumberChange = onRollNumberChange,
                    error = true,
                    sharedPreferences = sharedPreferences
                )
            }

        }
        builder.setCancelable(false) // do not allow dialog to be cancelled
        builder.show()
    }
}
