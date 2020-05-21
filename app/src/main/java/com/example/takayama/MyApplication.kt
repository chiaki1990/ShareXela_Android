package com.example.takayama

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response





var BASE_URL: String = ""
lateinit var sessionData: SessionData;
var navigationDrawerInit = false //false:未実行 -> MasterFragmentで実行する

/*
MyApplicationの役割は

settings.ktの内容によってBASE_URLを設定する

Contextクラスオブジェクトをいつでも呼べるappContextを設定する

ユーザーデータを格納するSessionDataを初期化すること
SPの情報としてログイン中ならば、そこにprofileObj, LogInStatus, authTokenHeaderを設定する
SPの情報としてログインでなければ、ログイン、ユーザー登録するまでSessionDataは更新されることはない。

 */



class MyApplication:Application() {


    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext


        if (devEnv){
            //BASE_URL = "http://10.0.2.2:8000/"
            //BASE_URL = "http://localhost:8000/"
            BASE_URL = "http://192.168.1.4:8000/"
        }else if (devEnv == false){
            BASE_URL = "https://sharexela.ga/"
        }


        sessionData = SessionData()


        //SPからLOGIN_STATUSを取得する
        val sharedPreferences = getSharedPreferencesInstance()
        val logInStatus = getLOGIN_STATUS(sharedPreferences)
        if (logInStatus == false) return

        //SPからauthTokenを取得
        val authToken = getAuthTokenFromSP(sharedPreferences)
        //authTokenHeaderを生成
        val authTokenHeader = getAuthTokenHeader(authToken)
        if (authTokenHeader == null) return


        val service = setService()

        service.loginWithAuthtoken(authTokenHeader).enqueue(object : Callback<CheckTokenResult>{


            override fun onResponse(call: Call<CheckTokenResult>, response: Response<CheckTokenResult>) {
                println("onResponseを通る")

                if (response.body()==null){
                    return
                }

                val result = response.body()!!.result
                if (result != "success") return

                val profileObj = response.body()!!.PROFILE_OBJ
                val key = response.body()!!.key
                val authTokenHeader = getAuthTokenHeader(key)
                if (authTokenHeader == null) return

                sessionData.profileObj = profileObj
                sessionData.logInStatus = true
                sessionData.authTokenHeader = authTokenHeader


                //SPにauthToken(key)を保存する
                val sharedPreferences = getSharedPreferencesInstance()
                val editor = sharedPreferences.edit()
                editor.putString(getString(R.string.SP_KEY_AUTH_TOKEN), key)
                editor.apply()

            }

            override fun onFailure(call: Call<CheckTokenResult>, t: Throwable) {
                println("onFailure")
                println(t)
                println(t.message)
            }

        })


    }


    companion object{

        lateinit var appContext: Context

    }

}