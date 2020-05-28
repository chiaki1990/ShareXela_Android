package ga.sharexela.sharexela

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onNewToken(token: String) {
        // 端末＋アプリを一意に識別するためのトークンを取得
        //Log.i("FIREBASE", "[SERVICE] Token = ${token ?: "Empty"}")
        //トークンが更新されたのでサーバーに送信する

        if (sessionData==null) return
        val service = setService()
        service.patchDeviceTokenDealAPIVeiw(sessionData.authTokenHeader, token).enqueue(object: Callback<ResultModel>{

            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                println("onResponseを通る : ")

            }
            override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                println("onFailureを通る : ")
            }
        })

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage?.let { message ->
            // 通知メッセージ
            message.notification?.let {
                // 通知メッセージを処理
            }


            // データメッセージ
            message.data?.let {
                // データメッセージを処理
            }



            /*
            //ログイン状態であれば通知一覧画面を表示する/そうでなければ最初の画面を開く
            if (sessionData.logInStatus == true) {
                val intent = Intent(MyApplication.appContext, NotificationActivity::class.java)
                startActivity(intent)
            }
            else if (sessionData.logInStatus == false){
                val intent = Intent(MyApplication.appContext, MasterActivity::class.java)
                startActivity(intent)
            }

             */

        }
    }
}