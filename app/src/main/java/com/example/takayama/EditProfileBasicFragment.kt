package com.example.takayama

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.fragment_edit_profile_basic.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [EditProfileBasicFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [EditProfileBasicFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
        val authTokenHeader = " Token " + authToken
        btnProfileConfirm.setOnClickListener {

            //入力内容を取得
            val inputUserName = etUserName.text.toString()
            val inputDescription = etDescription.text.toString()

            var profile = ProfileSerializerModel(UserSerializerModel())

            //入力内容とオブジェクトのプロパティが同一のものが含まれている場合、
            // Djangoのis_valid()がFalseになってしまう。
            //したがって同一のものは送らない仕組みに変更する。
            if (username != inputUserName ) {
                profile.user!!.username = inputUserName
            }
            if (description != inputDescription){
                profile.description = inputDescription
            }

            if (username == inputUserName && description == inputDescription){
                makeToast(MyApplication.appContext, "内容が変更されていません。")
            }else {

                //retrofitで送信する
                ServiceProfile.patchProfile(authToken=authTokenHeader, profile=profile, context=MyApplication.appContext )

                username = inputUserName
                description = inputDescription
            }
        }

    }


    override fun onResume() {
        super.onResume()
        //画面を描画する
        etUserName.setText(username)
        etDescription.setText(description)
        Glide.with(MyApplication.appContext).load(imageUrl).into(ivUserProfileImage)

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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditProfileBasicFragment.
         */
        // TODO: Rename and change types and number of parameters
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
