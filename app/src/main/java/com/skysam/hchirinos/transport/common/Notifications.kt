package com.skysam.hchirinos.transport.common

import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject

/**
 * Created by Hector Chirinos on 05/12/2022.
 */

object Notifications {
    const val NOTIFICATION_API = "https://fcm.googleapis.com/v1/projects/transport-5161f/messages:send"
    //const val NOTIFICATION_API = "https://skysam1.000webhostapp.com/transport/transport_RS.php"

    val PATH_NOTIFICATION = when(Classes.getEnviroment()) {
        Constants.DEMO -> Constants.TOPIC_ALL_NOTIFICATIONS_DEMO
        Constants.RELEASE -> Constants.TOPIC_ALL_NOTIFICATIONS
        else -> Constants.TOPIC_ALL_NOTIFICATIONS
    }

    private fun getInstance(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }

    fun subscribeToNotifications() {
        getInstance().subscribeToTopic(PATH_NOTIFICATION)
            .addOnSuccessListener {
                Log.e("MSG OK", "subscribe")
            }
    }

    fun unsubscribeToNotifications() {
        getInstance().unsubscribeFromTopic(PATH_NOTIFICATION)
    }

    fun sendNotification (title: String, body: String, topic: String) {
        val notificationJson = JSONObject()
        notificationJson.put(Constants.TITLE, title)
        notificationJson.put(Constants.BODY, body)

        val messageJson = JSONObject()
        messageJson.put(Constants.TOPIC, topic)
        messageJson.put(Constants.NOTIFICATION, notificationJson)

        val sendJson = JSONObject()
        sendJson.put(Constants.MESSAGE, messageJson)

        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, NOTIFICATION_API, sendJson,
            {
                response ->
                Log.e("MSG OK", response.toString())
            }, {
                error ->
                Log.e("Volley error", error.localizedMessage!!)
                Log.e("MSG ERROR", error.message!!)
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                headers["Authorization"] = "Bearer " + AccessToken.getAccessToken()
                return headers
            }
        }
        Transport.Transport.addToReqQueue(jsonObjectRequest)
        /*val params = JSONObject()
        params.put(Constants.METHOD, SEND_NOTIFICATION)
        params.put(Constants.TITLE, title)
        params.put(Constants.MESSAGE, message)
        params.put(Constants.TOPIC, topic)

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, NOTIFICATION_API, params,
            {response ->
                try {
                    when (response.getInt(Constants.SUCCESS)) {
                        Constants.SEND_NOTIFICATION_SUCCESS -> {
                            Log.e("MSG OK", response.getString(Constants.MESSAGE))
                        }
                        Constants.ERROR_METHOD_NOT_EXIST -> Log.e("MSG ERROR", response.getString(Constants.MESSAGE))
                        else -> Log.e("MSG ERROR", response.getString(Constants.MESSAGE))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("MSG ERROR", response.getString(Constants.MESSAGE))
                }
            }, {error ->
                Log.e("Volley error", error.localizedMessage!!)
                Log.e("MSG ERROR", error.message!!)
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }
        }
        Transport.Transport.addToReqQueue(jsonObjectRequest)*/
    }
}