package com.example.eathub

import android.util.Log
import com.google.gson.Gson
import java.net.URL

class Request(val url:String){

    fun request(): RecipeData{
        val requestData = URL(url).readText()
//        Log.e("TAG",requestData)
        val request = Gson().fromJson(requestData,RecipeData::class.java)
        return request
    }
}