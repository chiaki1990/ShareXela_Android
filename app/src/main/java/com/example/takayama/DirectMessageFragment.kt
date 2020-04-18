package com.example.takayama

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_direct_message.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



class DirectMessageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var itemObj: ItemSerializerModel? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemObj = it.getSerializable(ARG_PARAM1) as ItemSerializerModel
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_direct_message, container, false)
    }


    override fun onResume() {
        super.onResume()

        //APIアクセス 画面に反映
        val service = setService()
        val authTokenHeader = " Token " + authToken
        service.getDirectMessageContentListAPIView(authTokenHeader=authTokenHeader, itemObjId=itemObj!!.id!!)
            .enqueue(object : Callback<DirectMessageContentListAPIView>{

                override fun onResponse(call: Call<DirectMessageContentListAPIView>, response: Response<DirectMessageContentListAPIView>) {
                    println("onResponseを通る")

                    if (!response.isSuccessful){
                        println("FAIL")
                        return
                    }

                    val dataArrayList : ArrayList<DirectMessageContentSerializerModel> = response.body()!!.DM_CONTENT_OBJECTS_SERIALIZER

                    //リサイクラービューを実装する
                    val layoutManager = LinearLayoutManager(MyApplication.appContext)
                    recyclerViewDirectMessage.layoutManager = layoutManager
                    recyclerViewDirectMessage.adapter = MyDirectMessageRecyclerViewAdapter(dataArrayList, listener!!)


                    btnDirectMessage.setOnClickListener {
                        //入力データを取得する
                        val inputData:String = textInputEditTextDirectMessage.text.toString()
                        if (inputData == ""){
                            makeToast(MyApplication.appContext, "メッセージが入力されていません")
                            return@setOnClickListener
                        }

                        val directmessageContent = DirectMessageContentSerializerModel(content=inputData)

                        //DirectMessageContentインスタンスを生成するためのAPI通信を行う。
                        service.postDirectMessageContentAPIView(authTokenHeader=authTokenHeader, itemObjId=itemObj!!.id!!, directMessageContent=directmessageContent).enqueue(object :Callback<ResultModel>{

                            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                                println("onResponseを通る_DirectMessageFragment/postDirectMessageContentAPIView")
                                val result = response.body()!!.result
                                if (result == "success"){
                                    //Toastで送信できた旨を出力する
                                    makeToast(MyApplication.appContext, "メッセージを送信しました。")

                                    //画面を更新する。
                                    onResume()
                                }
                            }

                            override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                                println("onResponseを通る_DirectMessageFragment/postDirectMessageContentAPIView")
                                println(t)
                            }
                        })
                    }

                }

                override fun onFailure(call: Call<DirectMessageContentListAPIView>, t: Throwable) {
                    println("onFailureを通る")
                }
            })

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

        @JvmStatic
        fun newInstance(itemObj: ItemSerializerModel, param2: String) =
            DirectMessageFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, itemObj)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
