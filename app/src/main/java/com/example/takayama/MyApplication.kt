package com.example.takayama

import android.app.Application
import android.content.Context


// コメントを送信する。



//とりあえずコメントを表示させる仕組みをdetailに実装する。

//コメントの仕組みを変更する？？   ...djangoの方



//Avisoができたとき通知する仕組みを実装するのがまだ。...djangoの方

// pcの場合にemailをどうするか　駄目だったら増やせばいい。

// 問題はemailの送信相手をどうするか？と、
// どこが影響してくるかを調査すること。


//それか現状の仕組みを維持して誰にリプするか選ばせる。



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