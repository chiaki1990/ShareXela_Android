package com.example.takayama

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory







class ServiceProfile {




    companion object{


        fun patchProfile(authToken:String, profile:ProfileSerializerModel, context: Context){
            var retrofit: Retrofit = retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            var service = retrofit.create(ShareXelaService::class.java)
            service.patchProfile(authToken, profile).enqueue(object: Callback<profileResultModel> {

                override fun onResponse(call: Call<profileResultModel>, response: Response<profileResultModel>) {

                    println("onResponse")

                    println("callの確認")
                    println("call.request().headers() :" + call.request().headers())
                    //println("call.request().body() : " +  call.request().body())
                    println("responseの確認")
                    println("response.message : " + response.message())
                    println(response.body())


                    //送信が成功したら内容を反映させる(エリア設定が終わったらProfileデータを取得)
                    val profileObj = response.body()!!.PROFILE_OBJ

                    println("profileObjの更新をServiceProfile#patchで行う")
                    println(profileObj)

                    sessionData.profileObj = profileObj



                    //トーストで成功の旨を表示(overrideでToastを実行する)
                    makeToast(context,"変更しました")
                    //Toast.makeText(applicationContext,"変更を完了しました", Toast.LENGTH_SHORT).show()

                    //変更したらフラグメントを終わらせるのはどうか？
                    //終わらせるならreplaceは良くないのか？
                    //ProfileActivity().supportFragmentManager.beginTransaction().replace(R.id.frameLayoutProfile, ProfileListFragment.newInstance("","")).commit()



                }

                override fun onFailure(call: Call<profileResultModel>, t: Throwable) {

                    println(t.message)
                    //送信失敗している旨を表示(overrideでToastを実行する)
                    Toast.makeText(context,"送信に失敗しました。", Toast.LENGTH_SHORT)

                }
            })

        }


    }


}