package com.example.takayama

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.android.synthetic.main.fragment_sign_up.*
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
 * [SignUpFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */



class SignUpFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }




    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        btnSignUp.setOnClickListener {
            //入力データの取得

            //入力内容が入ってない場合を加味していないので後で考える
            val inputEmailAdderss: String = etEmailAdressSignUp.text.toString()
            val inputPassword1: String = etPassword1SignUp.text.toString()
            val inputPassword2: String = etPassword2SignUp.text.toString()

            //password1とpassword2が一致していない場合は何もしない(早期リターン)
            if (inputPassword1 != inputPassword2){
                //エラーを表示する
                //layoutInputの仕様を確認する


            }else{
                trySignUp(inputEmailAdderss, inputPassword1, inputPassword2)
            }



        }
    }




    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
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
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignUpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun trySignUp(email: String, password1: String, password2: String) {
        //password1とpassword2が一致しているので、考えるべきところはemailが使われていないかどうか

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(ShareXelaService::class.java)
        service.signup(email, password1, password2).enqueue(object :Callback<AuthModel>{

            override fun onResponse(call: Call<AuthModel>, response: Response<AuthModel>) {

                if (response.isSuccessful){
                    val key = response.body()?.key
                    authToken = key

                    //ユーザー登録できたのでauthTokenを端末に登録する -> SharedPreferencesに格納
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

                    val editor = sharedPreferences.edit()
                    editor.putString(getString(R.string.SP_KEY_AUTH_TOKEN), authToken).apply()
                    //格納できているか確認できていない。
                    println("確認用；authToken ...." + authToken)

                    println("確認用 : "+ sharedPreferences.getString(getString(R.string.SP_KEY_AUTH_TOKEN), "authTokenは存在しない"))

                }
            }

            override fun onFailure(call: Call<AuthModel>, t: Throwable) {
                println("onFailureの場合")
                println(t)

            }
        })
    }

}
