package ga.sharexela.sharexela

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_log_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
        Log.d("SignInの後の調査", "LogInFragment#onCreateを通過")
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_log_in, container, false)
        setHasOptionsMenu(true)
        return view
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.apply {
            findItem(R.id.menuSearch).isVisible = false
            findItem(R.id.menuGoHome).isVisible = false
            findItem(R.id.action_settings).isVisible = false
            findItem(R.id.menuDone).isVisible = false

        }
    }




    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("SignInの後の調査", "LogInFragment#onActivityCreateｄを通過")

        //ログインボタンを押したら発動するリスナーを設置
        setBtnLogInListener()

        //サインアップボタンを押したら発動するリスナーを設置
        setBtnSignUpListener()
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

        //SignUpアクティビティを起動する
        fun launchSignUpActivity()
    }

    companion object {


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
            progressBarLogIn.visibility = View.VISIBLE
            logInByBasicAuth(inputEmailAddress, inputPassword)
        }
    }

    private fun setBtnSignUpListener(){
        btnLogInForSignUp.setOnClickListener {
            listener!!.launchSignUpActivity()
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


                    //FCMのデバイストークンを取得、送信する
                    sendDeviceToken()



                    //ログイン成功がしたのでProfileSerializerModelオブジェクトを取得するために以下メソッドを実行
                    listener!!.getProfileSerializerModel()


                } else if (response.isSuccessful == false) {
                    progressBarLogIn.visibility = View.GONE
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
                progressBarLogIn.visibility = View.GONE
                println("エラーハンドリング")
                //認証情報が正しくありませんと表示するか?
            }
        })

    }


    override fun onPause() {
        super.onPause()
        //ソフトキーボードを非表示
        hideKeybord(this)
    }

    override fun onDestroy() {
        Log.d("SignInの後の調査", "LogInFragment#onDestroyを通過")
        super.onDestroy()
    }

}



