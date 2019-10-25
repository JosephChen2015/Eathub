package com.example.eathub

import android.util.Log
import com.google.gson.Gson
import java.net.URL
import com.google.gson.reflect.TypeToken



class Request(val url:String){

    fun request(): RecipeData{
        val requestData = URL(url).readText()
        val request = Gson().fromJson(requestData,RecipeData::class.java)
        return request
    }

    fun requestIn(): Instruction{
        val rData = URL(url).readText()
        Log.e("TAG",rData)
        val collectionType = object : TypeToken<Collection<Instruction>>() {

        }.type
        val enums:Collection<Instruction> = Gson().fromJson(rData,collectionType)
        if (enums.size > 0) {
            return enums.elementAt(0)
        } else {
            return Instruction("noInstruction",emptyList<Step>())
        }

    }

    fun requestWeather(): WeatherData{
        val requestData = URL(url).readText()
        val request = Gson().fromJson(requestData,WeatherData::class.java)
        return request
    }
}