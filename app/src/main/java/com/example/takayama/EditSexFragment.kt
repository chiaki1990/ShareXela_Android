package com.example.takayama

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_edit_sex.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"







class EditSexFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_edit_sex, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Cambiarボタンを押したらretrofitでサーバーに送信する

        //リスナーの設定
        btnChangeSex.setOnClickListener {

            //RadioGroupでセットされている。
            when (rgSex.checkedRadioButtonId){
                R.id.rbNoAnswer -> ServiceProfile.patchProfile(authToken= sessionData.authTokenHeader!!, profile= ProfileSerializerModel(sex=0), context=MyApplication.appContext)
                R.id.rbMale -> ServiceProfile.patchProfile(authToken= sessionData.authTokenHeader!!, profile=ProfileSerializerModel(sex=1), context=MyApplication.appContext)
                R.id.rbFemale -> ServiceProfile.patchProfile(authToken= sessionData.authTokenHeader!!, profile=ProfileSerializerModel(sex=2), context=MyApplication.appContext)

            }
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





    interface OnFragmentInteractionListener {

    }

    companion object {


        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditSexFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
