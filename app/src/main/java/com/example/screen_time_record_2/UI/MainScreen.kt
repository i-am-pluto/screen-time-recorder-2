package com.example.screen_time_record_2.UI

import android.app.Activity
import android.app.AlertDialog
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.screen_time_record_2.R
import com.example.screen_time_record_2.database.ViewModel
import com.example.screen_time_record_2.services.BackendApiService
import com.example.screen_time_record_2.services.UserStatsService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import java.time.LocalDate

class MainScreen(
    private val activity: Activity,
    private val context: Context,
    private val db: ViewModel,
    private val mainActivity: LifecycleOwner,
    private val sharedPreferences: SharedPreferences,
    private val backendApiService: BackendApiService,
) {


    private val titles = Titles()
    private val textFields = TextFields(activity = activity, backendApiService = backendApiService)
    private val cards = Cards()

    private var stats = MutableLiveData<Map<String, String>>()

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun ScreenTimeRecordScreen() {


        var date by remember {
            mutableStateOf(LocalDate.now())
        }

        var rollNumber by rememberSaveable {
            mutableStateOf("")
        }

        rollNumber = sharedPreferences.getString("RollNumber", "").toString()

        val userStatsService = UserStatsService(
            date = date,
            context = context,
            db = this.db,
        )
        this.stats = userStatsService.getStats()

        var stats1 = remember { mutableStateMapOf<String, String>() }
        this.stats.observe(mainActivity, Observer {
            stats1.putAll(it)
        })


        val appOps: AppOpsManager =
            context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val mode = appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
            if (mode == AppOpsManager.MODE_ALLOWED) {
                // Permission is granted
            } else if (mode == AppOpsManager.MODE_DEFAULT) {
                // Permission is not granted, ask for it
                allowUsagePermissionDialog()


            } else {
                // Permission is denied
                // Show the dialog to make the user open the settings and grant the permission
                showUsagePermissionDeniedDialog()
            }
        } else {
            val appOpsClass: Class<*> = AppOpsManager::class.java
            val checkOpNoThrowMethod = appOpsClass.getMethod(
                "checkOpNoThrow",
                Integer.TYPE,
                Integer.TYPE,
                String::class.java
            )
            val op = AppOpsManager.OPSTR_GET_USAGE_STATS
            val mode =
                checkOpNoThrowMethod.invoke(appOps, op, Process.myUid(), context.packageName) as Int
            if (mode == AppOpsManager.MODE_ALLOWED) {
                // Permission is granted
            } else if (mode == AppOpsManager.MODE_DEFAULT) {
                // Permission is not granted, ask for it
                val setModeMethod = appOpsClass.getMethod(
                    "setMode",
                    Integer.TYPE,
                    Integer.TYPE,
                    String::class.java,
                    Integer.TYPE
                )
                setModeMethod.invoke(
                    appOps,
                    op,
                    Process.myUid(),
                    context.packageName,
                    AppOpsManager.MODE_ALLOWED
                )
            } else {
                // Permission is denied
                // Show the dialog to make the user open the settings and grant the permission
                showUsagePermissionDeniedDialog()
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            titles.Title(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 40.dp)
                    .fillMaxWidth()
            )


            textFields.EnterRollNumber(
                rollNumber = rollNumber,
                onRollNumberChange = { rollNumber = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp, horizontal = 30.dp),
                sharedPreferences = sharedPreferences
            )

            cards.MainCard(
                date = date,
                stats = stats1,
                onDateChange = { date = it },
                modifier = Modifier
            )

        }

    }

    private fun allowUsagePermissionDialog() {
        val builder = MaterialAlertDialogBuilder(activity,R.style.DialogTheme)
        builder.setTitle("Usage Access Permission Required")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setMessage("To use this app, you must grant usage access permission. Please follow the instructions below to enable this permission:\n\n1. Open Settings\n2. Select Apps & notifications\n3. Select Special app access\n4. Select Usage access\n5. Find this app and turn on usage access\n\nIf you have any questions or need further assistance, please contact support.")
        builder.setPositiveButton(android.R.string.ok) { dialog, which ->


            dialog.dismiss()

            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)

        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun showUsagePermissionDeniedDialog() {
        val builder = MaterialAlertDialogBuilder(activity, R.style.DialogTheme)
        builder.setTitle("Usage Access Permission Denied")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setMessage("To use this app, you must grant usage access permission. Please open Settings, navigate to the App Permissions section, and grant permission to this app.")
        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
            dialog.dismiss()
        }
        builder.setCancelable(false)
        builder.show()
    }

}