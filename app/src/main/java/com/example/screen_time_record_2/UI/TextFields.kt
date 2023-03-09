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
import androidx.compose.ui.unit.dp
import com.example.screen_time_record_2.R
import com.example.screen_time_record_2.services.BackendApiService


class TextFields(private val activity: Activity, private val backendApiService: BackendApiService) {

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
                sharedPreferences = sharedPreferences,
                backendApiService = backendApiService
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
//            Button(
//                onClick = {
//                    enterRollNumberAlert(
//                        rollNumber = rollNumber,
//                        onRollNumberChange = onRollNumberChange,
//                        sharedPreferences = sharedPreferences
//                    )
//                },
//                modifier = Modifier
//                    .weight(1f),
//                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.baseline_edit_24),
//                    contentDescription = null
//                )
//            }
        }

    }


    private fun enterRollNumberAlert(
        rollNumber: String,
        onRollNumberChange: (String) -> Unit,
        error: String = "",
        sharedPreferences: SharedPreferences,
        backendApiService: BackendApiService
    ) {
        var builder = AlertDialog.Builder(activity)
        builder.setTitle("Nsut Roll Number")
        builder.setIcon(R.drawable.baseline_edit_24)
        builder.setMessage("Must enter NSUT Roll Number for the application to work!!")

        val input = EditText(activity)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        if(error != "") {
            input.error = error
        }
        builder.setPositiveButton(
            android.R.string.ok
        ) { dialog, which ->
            var temp = input.text.toString()

            var check = temp.matches(Regex("[0-9]{4}[A-Za-z]{3}[0-9]{4}"))

            backendApiService.doesUserExists(temp).observeForever { isUser ->
                if (check && isUser==false) {
                    onRollNumberChange(temp)
                    var editor = sharedPreferences.edit()
                    editor.putString("RollNumber", temp)
                    editor.commit();
                } else if (!check) {
                    dialog.dismiss()
                    enterRollNumberAlert(
                        rollNumber = rollNumber,
                        onRollNumberChange = onRollNumberChange,
                        error = "Enter your NSUT RollNumber only example - 2020UCA1891",
                        sharedPreferences = sharedPreferences,
                        backendApiService = this.backendApiService
                    )
                } else if(isUser == true){
                    dialog.dismiss()
                    enterRollNumberAlert(
                        rollNumber = rollNumber,
                        onRollNumberChange = onRollNumberChange,
                        error = "A user with the same roll number already exists. enter a differant roll number of example - 2020UCA1891",
                        sharedPreferences = sharedPreferences,
                        backendApiService = this.backendApiService
                    )
                }

            }


        }
        builder.setCancelable(false) // do not allow dialog to be cancelled
        builder.show()
    }
}
