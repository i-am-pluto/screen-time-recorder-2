package com.example.screen_time_record_2.services

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject


class BackendApiService(
    private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val url: String,
    private val rollNumber: String,
) {

    private val cache = DiskBasedCache(context.cacheDir, 1024 * 1024)
    private val network = BasicNetwork(HurlStack())

    private val requestQueue = RequestQueue(cache, network).apply {
        start()
    }

    fun getLatestUpdatedStatDate(): MutableLiveData<String> {
        // get the date

        var date = MutableLiveData<String>()

        val body = JSONObject().apply {
            put("roll_number", rollNumber)
        }

        try {
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, body,
                { response ->
                    date.postValue(response.getJSONObject("date").toString())
                },
                { error ->
                    println(error)
                },

            )

            this.requestQueue.add(jsonObjectRequest)

        } catch (e: java.lang.Exception) {
            e.printStackTrace();
        }

        return date;

    }


    fun sendStats(stats: Map<String, Map<String, String>>) {
        val body = JSONObject(stats as Map<String, Any>)
        try {

            var res = MutableLiveData<String>()

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, body,
                { response ->
                    res.postValue(response.getJSONObject("message").toString())
                },
                { error ->
                    println(error)
                },
            )
            this.requestQueue.add(jsonObjectRequest)


        } catch (e: java.lang.Exception) {
            e.printStackTrace();
        }

    }
}