package com.example.takayama

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_edit_mail_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


//このアクティビティでは２つの変更を実現する
//1つは、MailAddressの変更である。
//MailAddressの変更はrest-authに実装されていないので自ら実装を行う。
//django-all-authの機能を利用することができる可能性がある。


//実装プラン
//①djangoにおいてmail機能が実装されていないのでmail機能を実装する。
//そのためにもdjango-sendgridインストールが必要か？
//②sendgridで登録事項があれば登録しておく。
//③djangoで登録できたらandroid実装にうつる。


//実装プランのメモ


//改善案
//入力内容がない場合ボタンを押せない


//もう1つは、Passwordの変更である。(実装済み)
//こちらは、rest-authにエンドポイントが実装されているので利用すれば良い。

//実装プラン
//①まず、djangoにおいてエンドポイントでパスワードの変更ができるか確認する。
// エンドポイント：/rest-auth/password/change/ (POST)
//②android側でretrofit全般の仕組みを構築する
//③ktファイルにロジックを実装する


//実装プランのメモ
//ユーザー認証されていない状態で/rest-auth/password/change/を実行しても以下のエラーが出る
//"detail": "Authentication credentials were not provided."
//上記はブラウザのセッションを切った状態でアクセスしたものである。
//TOKENをセットして/rest-auth/password/change/を試した場合パスワードの変更ができた。
//これらの情報からandroid側で実装することができると判断する。
//パスワード変更した場合のレスポンスは　　{"detail":"New password has been saved."} 200
//旧パスワードが誤りの場合のレスポンスは　{"old_password":["Invalid password"]} 400
//トークンが不適切な場合のレスポンスは　　{"detail":"Invalid token."} 401


//改善案
//入力内容がない場合ボタンを押せない

//passwordリセットの際は、emailが送信されるよう。この送信についてどうなっているのか調べる。
//上はパスワードを忘れた場合じゃなくて？











// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [EditMailPasswordFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [EditMailPasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditMailPasswordFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_edit_mail_password, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //各ボタンのクリック処理を実装

        btnChangeEmail.setOnClickListener {

            //入力したEmailAddressを取得する
            var email: String = etEmailAddress.text.toString()

            //ProfileModelオブジェクトを生成する
            var profile = ProfileSerializerModel(UserSerializerModel(email=email))

            //retrofitでEmailAddress変更のAPIをたたく
            ServiceProfile.patchProfile(authToken!!, profile, MyApplication.appContext)
        }


        btnCambiarPassword.setOnClickListener{

            //入力したパスワードを取得する
            var oldPassword: String = etCurrentPassword.text.toString()
            var newPassword1: String = etNewPassword1.text.toString()
            var newPassword2: String = etNewPassword2.text.toString()

            if (newPassword1 != newPassword2) {
                //変更後パスワードが一致していない場合には、入力内容を削除し、Toastで再入力を促す
                //etCurrentPassword.setText("")
                //etNewPassword1.setText("")
                //etNewPassword2.setText("")
                deleteEditTextContent()

                makeToast(MyApplication.appContext, "新しいパスワードが一致していません。")

            } else if(newPassword1 == newPassword2) {
                //ChangePasswordModelを生成する
                var changePasswordModel = ChangePasswordModel(
                    new_password1 = newPassword1,
                    new_password2 = newPassword2,
                    old_password = oldPassword
                )

                //retrofitでpassword変更のAPIをたたく
                changePassword(authToken!!, changePasswordModel)
            }
        }

    }



    override fun onResume() {
        super.onResume()
        //EmailAddressを表示する
        etEmailAddress.setText(email)
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



    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditMailPasswordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditMailPasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun deleteEditTextContent() {
        etCurrentPassword.setText("")
        etNewPassword1.setText("")
        etNewPassword2.setText("")
    }


    private fun changePassword(authToken: String, changePasswordModel: ChangePasswordModel) {

        val service = setService()


        //callbackにchangePasswordResponseModelを使ってるけど必要ないかも。またはResultModelの属性を拡張してしまえば済話かもしれない。
        service.changePassword(authToken, changePasswordModel).enqueue(object:
            Callback<ChangePasswordResponseModel> {

            override fun onResponse(
                call: Call<ChangePasswordResponseModel>,
                response: Response<ChangePasswordResponseModel>
            ) {
                println(response.body())
                if (response.isSuccessful){
                    //password変更ができた場合=>Toastで結果を表示する
                    makeToast(MyApplication.appContext,"Passwordを変更しました。")
                    deleteEditTextContent()
                }else if(response.code() == 400){
                    //旧パスワードの入力が誤っている場合に該当=>Toastで結果を表示する
                    makeToast(MyApplication.appContext,"旧パスワードが異なります。")
                    deleteEditTextContent()
                }else if(response.code() == 401){
                    //トークンに誤りがある場合、認証ができなかった場合
                    deleteEditTextContent()
                }else{
                    deleteEditTextContent()
                }

            }

            override fun onFailure(call: Call<ChangePasswordResponseModel>, t: Throwable) {
                //TODO("not implemented")
            }
        })

    }




}
