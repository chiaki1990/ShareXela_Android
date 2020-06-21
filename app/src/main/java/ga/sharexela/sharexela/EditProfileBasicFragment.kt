package ga.sharexela.sharexela

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_edit_profile_basic.*



private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




class EditProfileBasicFragment : Fragment() {
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


        /*
        activity!!.actionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.apply{
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener {
                //フラグメントの削除
                fragmentManager!!.beginTransaction().remove(this@EditProfileBasicFragment).commit()
            }
        }

         */
        return inflater.inflate(R.layout.fragment_edit_profile_basic, container, false)
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        //画像を変更させる（画像をクリックして変更させる）
        ivUserProfileImage.setOnClickListener { listener!!.onClickProfileImage() }


        //送信データを押したら変更を始める
        btnProfileConfirm.setOnClickListener {

            //入力内容を取得
            val inputUserName    = etUserName.text.toString()
            val inputDescription = etDescription.text.toString()

            val profileObjForSend = ProfileSerializerModel(UserSerializerModel())

            //入力内容とオブジェクトのプロパティが同一のものが含まれている場合、
            // Djangoのis_valid()がFalseになってしまう。
            //したがって同一のものは送らない仕組みに変更する。
            if (sessionData.profileObj!!.user!!.username != inputUserName ) {
                profileObjForSend.user!!.username = inputUserName
            }
            if (sessionData.profileObj!!.description != inputDescription){
                profileObjForSend.description = inputDescription
            }

            if (sessionData.profileObj!!.user!!.username == inputUserName && sessionData.profileObj!!.description == inputDescription){
                makeToast(MyApplication.appContext, "内容が変更されていません。")
            }else {

                //retrofitで送信する
                ServiceProfile.patchProfile(authToken=sessionData.authTokenHeader!!, profile=profileObjForSend, context=MyApplication.appContext )

            }
        }

    }


    override fun onResume() {
        super.onResume()
        //画面を描画する

        etUserName.setText(sessionData.profileObj!!.user!!.username)
        etDescription.setText(sessionData.profileObj!!.description)
        val imageUrl = BASE_URL + sessionData.profileObj!!.image!!.substring(1)
        GlideApp.with(MyApplication.appContext).load(imageUrl).circleCrop().into(ivUserProfileImage)
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
        fun onClickProfileImage()
    }

    companion object {


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditProfileBasicFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
