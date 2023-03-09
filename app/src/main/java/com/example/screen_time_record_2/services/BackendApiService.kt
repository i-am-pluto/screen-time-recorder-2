package com.example.screen_time_record_2.services

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyLog
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONException
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
        VolleyLog.DEBUG = true;


        var date = MutableLiveData<String>()

        if (rollNumber == "") return date


        try {
            val reqUrl = "$url/latestupdate?roll_number=$rollNumber"
            Log.d("url", reqUrl)

            val jsonObjectRequest =
                JsonObjectRequest(Request.Method.GET, reqUrl, null, { response ->
                    Log.d("response", response.toString())

                    try {

                        if (response.get("success") == true) {
                            date.postValue(response.get("date").toString())
                        } else {
                            Log.d("Error", response.get("message").toString())
                        }
                    } catch (e: JSONException) {
                        throw RuntimeException("Error parsing response JSON", e)
                    }

                }, { error ->
                    println(error)
                })

            this.requestQueue.add(jsonObjectRequest)

        } catch (e: java.lang.Exception) {
            e.printStackTrace();
        }

        return date;

    }

    fun sendStats(stats: Map<String, Map<String, String>>) {
        val body = JSONObject()
        if (rollNumber == "")
            return

        val list = stats.map { (date, usage) ->
            mapOf(
                "date" to date,
                "night_use" to (usage["night_use"] ?: "0:00"),
                "day_use" to (usage["day_use"] ?: "0:00"),
                "unlocks" to (usage["unlocks"] ?: "0")
            )
        }

        body.put("stats", JSONArray(list))
        body.put("roll_number", rollNumber);
        try {

            var res = MutableLiveData<String>()

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, "$url/savestats", body,
                { response ->

                    if(response.get("success")==true) {
                        res.postValue(response.get("message").toString())
                    } else {
                        println(response.get("message"))
                    }
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

    fun doesUserExists(rollNumber: String): MutableLiveData<Boolean> {

        var isUser = MutableLiveData<Boolean>()

        try {
            val reqUrl = "$url/doesuserexists?roll_number=$rollNumber"
            Log.d("url", reqUrl)



            val jsonObjectRequest =
                JsonObjectRequest(Request.Method.GET, reqUrl, null, { response ->
                    Log.d("response", response.toString())

                    try {

                        if (response.get("success") == true) {
                            isUser.postValue((response.get("result") as Boolean))
                        } else {
                            Log.d("Error", response.get("message").toString())
                        }
                    } catch (e: JSONException) {
                        throw RuntimeException("Error parsing response JSON", e)
                    }

                }, { error ->
                    println(error)
                })

            this.requestQueue.add(jsonObjectRequest)

        } catch (e: java.lang.Exception) {
            e.printStackTrace();
        }

        return isUser
    }

}