package com.example.takayama

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.security.crypto.MasterKeys
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.security.crypto.EncryptedSharedPreferences
import kotlinx.android.synthetic.main.fragment_log_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory





// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"





class LogInFragment : Fragment() {




    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_log_in, container, false)
        setHasOptionsMenu(true)
        return view
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



        //ログインボタンを押したら発動するリスナーを設置
        setBtnLogInListener()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }




    interface OnFragmentInteractionListener {

        // そのままLogInActivityを終了する
        //fun resultSuccessLogIn()

        // profileObjを取得してLogInActivityを終了する
        fun getProfileSerializerModel()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LogInFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LogInFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }




    private fun setBtnLogInListener() {
        btnLogIn.setOnClickListener {
            //入力データの取得
            var inputEmailAddress:String = etEmailAddressLogIn.text.toString()
            var inputPassword:String = etPasswordLogIn.text.toString()
            logInByBasicAuth(inputEmailAddress, inputPassword)
        }
    }




    private fun logInByBasicAuth(email: String, password: String) {


        val service = setService()
        println(email+" : "+ password)
        service.login(email, password).enqueue(object: Callback<AuthModel> {
            override fun onResponse(call: Call<AuthModel>, response: Response<AuthModel>) {
                println("onResponseを通る")
                println(call.request().headers())
                println(call.request().body())
                //BasicAuthにおいて認証データが適切な場合にはステータス：200が返される。
                //逆に不適切な場合にはステータス：400が返される。
                //したがって認証情報が不適切な場合(401)にはnon_field_errorsが返されることは全くない



                println(response.code())
                if (response.isSuccessful) {
                    //println(response.body())
                    //println(response.body()?.key)
                    val key = response.body()?.key

                    println("onResponseメソッド内のauthToken")
                    println("Authtoken " + key)

                    //SPにAuthTokenを保存する
                    val sharedPreferences = getSharedPreferencesInstance()
                    val editor = sharedPreferences.edit()
                    editor.putString(getString(R.string.SP_KEY_AUTH_TOKEN), key)
                    editor.apply()

                    //SessionDataオブジェクトのauthTokenHeader属性値を更新する
                    val authTokenHeader = getAuthTokenHeader(key)
                    if (authTokenHeader == null) return
                    sessionData.authTokenHeader = authTokenHeader


                    //ログイン成功がしたのでProfileSerializerModelオブジェクトを取得するために以下メソッドを実行
                    listener!!.getProfileSerializerModel()


                } else if (response.isSuccessful == false) {
                    //nullが返される
                    println(response.errorBody())//?.non_field_errors
                    println(response.message())

                    //誤りの場合にはToastを表示してログイン画面は継続させる
                    Toast.makeText(
                        MyApplication.appContext,
                        getString(R.string.login_fail_message),
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }

            override fun onFailure(call: Call<AuthModel>, t: Throwable) {
                println("エラーハンドリング")
                //認証情報が正しくありませんと表示するか?
            }
        })

    }

}



