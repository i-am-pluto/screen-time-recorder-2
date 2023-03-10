package com.example.screen_time_record_2.UI

import android.app.Activity
import android.app.AlertDialog
import android.content.SharedPreferences
import android.text.InputType
import android.widget.EditText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.screen_time_record_2.R
import com.example.screen_time_record_2.services.BackendApiService
import com.google.android.material.dialog.MaterialAlertDialogBuilder


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
                onValueChange = onRollNumberChange,
                label = { Text(text = "NSUT ROLL NUMBER") },
                modifier = Modifier
                    .weight(4f)
                    .padding(10.dp),
                enabled = false,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    disabledTextColor = Color.Gray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorCursorColor = Color.Red,
                    errorIndicatorColor = Color.Red
                ),
                textStyle = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                shape = MaterialTheme.shapes.medium,
                singleLine = true,
            )
        }

    }


    private fun enterRollNumberAlert(
        rollNumber: String,
        onRollNumberChange: (String) -> Unit,
        error: String = "",
        sharedPreferences: SharedPreferences,
        backendApiService: BackendApiService
    ) {
        val builder = MaterialAlertDialogBuilder(activity,R.style.DialogTheme)
        builder.setTitle("Enter NSUT Roll Number")
        builder.setIcon(R.drawable.baseline_edit_24)

        val input = EditText(activity).apply {
            inputType = InputType.TYPE_CLASS_TEXT
            hint = "NSUT Roll Number"
            setText(rollNumber)
            setSelection(rollNumber.length)
            if (error.isNotEmpty()) {
                this.error = error
            }
        }
        builder.setView(input)

        builder.setPositiveButton("Submit") { dialog, which ->
            val temp = input.text.toString().trim()

            val isValidRollNumber = temp.matches(Regex("[0-9]{4}[A-Za-z]{3}[0-9]{4}"))

            backendApiService.doesUserExists(temp).observeForever { isUser ->
                when {
                    isValidRollNumber && !isUser -> {
                        onRollNumberChange(temp)
                        backendApiService.setRollNumber(temp)
                        sharedPreferences.edit().putString("RollNumber", temp).commit()
                    }
                    !isValidRollNumber -> {
                        enterRollNumberAlert(
                            rollNumber = rollNumber,
                            onRollNumberChange = onRollNumberChange,
                            error = "Invalid NSUT Roll Number format. Example - 2020UCA1891",
                            sharedPreferences = sharedPreferences,
                            backendApiService = backendApiService
                        )
                    }
                    else -> {
                        enterRollNumberAlert(
                            rollNumber = rollNumber,
                            onRollNumberChange = onRollNumberChange,
                            error = "User with same Roll Number already exists. Enter different Roll Number. Example - 2020UCA1891",
                            sharedPreferences = sharedPreferences,
                            backendApiService = backendApiService
                        )
                    }
                }
            }
        }
        builder.setCancelable(false)
        builder.show()
    }

}
