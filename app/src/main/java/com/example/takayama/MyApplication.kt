package com.example.takayama

import android.app.Application
import android.content.Context


//val BASE_URL: String = "https://sharexela.ga/"
val BASE_URL: String = "http://10.0.2.2:8000/"
var authToken: String? = "";



class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object{
        lateinit var appContext: Context
        var loginStatus: Boolean = false //ログインしたらtrueに変更する
    }
}