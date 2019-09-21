package com.chodingcoding.happygraudate

import com.chodingcoding.happygraudate.navigation.module.PushDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException

class FcmPush(){

    var JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
    var url = "https://fcm.googleapis.com/fcm/send"
    var serverKey = "AAAAnriL3UI:APA91bEEYk__gpVLsV4cMM7UANJq1AxB45GfMA8PuNuqbdi5DyTHVzG7M3L7atOopjoUVQtJ1iFvAac8CK5scWdE15IWlLE9cLRJU7iZelJxKlIEnD_D1AHzWkNx5yT0gWwDho-Tk1cN"

    var okHttpClient : OkHttpClient? = null
    var gson : Gson? = null

    init {
        gson = Gson()
        okHttpClient = OkHttpClient()



    }

    fun sendMessage(destinationUid:String, title:String?, message:String?){

        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get().addOnCompleteListener {

            task ->
            if(task.isSuccessful){
                var token = task.result!!["pushToken"].toString()
                var pushDTO = PushDTO()
                pushDTO.to = token
                pushDTO.notification?.title = title
                pushDTO.notification?.body = message

                var body = gson?.toJson(pushDTO)?.let { RequestBody.create(JSON, it) }

                var request = Request.Builder().addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "key=$serverKey")
                    .url(url)
                    .post(body!!)
                    .build()
                okHttpClient?.newCall(request)?.enqueue(object : Callback{
                    override fun onFailure(call: Call, e: IOException) {

                        //인터넷 연결에 실패했을 때
                    }

                    override fun onResponse(call: Call, response: Response) {
                        println(response.body?.string())
                    }

                })





            }

        }
    }
}