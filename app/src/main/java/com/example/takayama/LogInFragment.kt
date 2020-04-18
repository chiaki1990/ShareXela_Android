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

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [LogInFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [LogInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */






class LogInFragment : Fragment() {



    val SP_XML = "SESSION_MANAGE"
    lateinit var sharedPreferences: SharedPreferences;
    var masterKeyAlias = "";


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


        authToken = getAuthTokenFromSP()


        if (authToken != null){
            //AuthTokenloginAPIを使ったを実施
            logInByAuthToken(authToken!!)

        }else{
            //authTokenがnullの場合-> ログイン画面の表示+Basic認証(logInByBasicAuth()メソッドの実施)

            //ログインボタンを押したら発動するリスナーを設置
            setBtnLogInListener()
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {

        fun resultSuccessLogIn()
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

    private fun getAuthTokenFromSP(): String? {
        //SPからauthTokenを取り出す
        //println("BUILD_NUMBERを表示")
        //println(Build.VERSION.SDK_INT)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            sharedPreferences = MyApplication.appContext.getSharedPreferences(SP_XML, Context.MODE_PRIVATE)

        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            sharedPreferences = EncryptedSharedPreferences.create(
                getString(R.string.LOGIN_SHARED_PREFERENCES),
                masterKeyAlias,
                MyApplication.appContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }

        authToken = sharedPreferences.getString(getString(R.string.SP_KEY_AUTH_TOKEN), null)
        println("端末内のTOKENデータを表示       " +  authToken)
        return authToken
    }

    private fun setBtnLogInListener() {
        btnLogIn.setOnClickListener {
            //入力データの取得
            var inputEmailAddress:String = etEmailAddressLogIn.text.toString()
            var inputPassword:String = etPasswordLogIn.text.toString()
            logInByBasicAuth(inputEmailAddress, inputPassword)
        }
    }

    private fun logInByAuthToken(authToken: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val authTokenHeader = " Token " + authToken
        val service = retrofit.create(ShareXelaService::class.java)
        service.loginWithAuthtoken(authTokenHeader).enqueue(object :Callback<AuthModel>{

            override fun onResponse(call: Call<AuthModel>, response: Response<AuthModel>) {
                print("レスポンスを回収" +response.code().toString())

                println(call.request().headers())
                if (response.isSuccessful) {
                    //println(response.body())
                    println(response.body()?.key)

                    var newToken = response.body()?.key

                    println("NEW_TOKEN" + newToken)

                    //SPにAuthTokenを保存する
                    val editor = sharedPreferences.edit()
                    editor.putString(getString(R.string.SP_KEY_AUTH_TOKEN), newToken)
                    editor.apply()



                    //ログイン成功がしたので、LogInActivityでfinish()を実行
                    listener!!.resultSuccessLogIn()


                }else if(response.isSuccessful == false) {

                    //失敗したのでログイン画面を表示させる
                    //buttonのリスナー設置
                    setBtnLogInListener()

                }
            }

            override fun onFailure(call: Call<AuthModel>, t: Throwable) {
                println("AuthToken login によるエラーハンドリング")
                //失敗したのでログイン画面を表示させる
                //buttonのリスナー設置
                setBtnLogInListener()

            }


        })

    }

    private fun logInByBasicAuth(email: String, password: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        val service = retrofit.create(ShareXelaService::class.java)
        //println(email+" : "+ password)
        service.login(email, password).enqueue(object: Callback<AuthModel> {
            override fun onResponse(call: Call<AuthModel>, response: Response<AuthModel>) {
                //BasicAuthにおいて認証データが適切な場合にはステータス：200が返される。
                //逆に不適切な場合にはステータス：400が返される。
                //したがって認証情報が不適切な場合(401)にはnon_field_errorsが返されることは全くない


                print("レスポンスを回収")
                println(response.code())
                if (response.isSuccessful) {
                    //println(response.body())
                    //println(response.body()?.key)
                    authToken = response.body()?.key

                    println("onResponseメソッド内のauthToken")
                    println("Authtoken " +authToken)
                    //SPにAuthTokenを保存する

                    val editor = sharedPreferences.edit()
                    editor.putString(getString(R.string.SP_KEY_AUTH_TOKEN), authToken)
                    editor.apply()

                    //ログイン成功がしたので、LogInActivityでfinish()を実行
                    listener!!.resultSuccessLogIn()

                } else if (response.isSuccessful == false) {
                    //nullが返される
                    println(response.errorBody().toString())//?.non_field_errors
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



