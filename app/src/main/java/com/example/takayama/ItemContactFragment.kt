package com.example.takayama

import android.content.Context
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
import java.io.Serializable


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




class ItemContactFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var itemContactObjects: ArrayList<ItemContactSerializerModel>? = null
    private var itemObj: ItemSerializerModel? = null
    private var listener: OnFragmentInteractionListener? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemContactObjects = it.getSerializable(ARG_PARAM1) as ArrayList<ItemContactSerializerModel>? //ItemContactListAPIViewModel
            itemObj            = it.getSerializable(ARG_PARAM2) as ItemSerializerModel
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // original return inflater.inflate(R.layout.fragment_item_contact, container, false)
        return inflater.inflate(R.layout.fragment_direct_message, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpRecyclerView(itemContactObjects!!)

        btnDirectMessage.setOnClickListener(this@ItemContactFragment)

    }


    override fun onResume() {
        super.onResume()
        //itemObjを使って画面を更新する。今はまだ書いていない。
        val service = setService()
        service.getItemContactListAPIView(sessionData.authTokenHeader!!, itemObj!!.id!!.toInt()).enqueue(object :Callback<ItemContactListAPIViewModel>{

            override fun onResponse(call: Call<ItemContactListAPIViewModel>, response: Response<ItemContactListAPIViewModel>) {
                println("onResponseを通過")
                val itemContactObjects = response.body()!!

                setUpRecyclerView(itemContactObjects.ITEM_CONTACT_OBJECTS)

                btnDirectMessage.setOnClickListener(this@ItemContactFragment)
            }

            override fun onFailure(call: Call<ItemContactListAPIViewModel>, t: Throwable) {
                println("onFailureを通過")
                println(t)
                println(t.message)
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

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(itemContactObjects: Serializable, itemObj: Serializable) =
            ItemContactFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, itemContactObjects)
                    putSerializable(ARG_PARAM2, itemObj)
                }
            }
    }



    override fun onClick(v: View?) {
        //メッセージボタンを押したらデータを送信し、画面を更新する。
        //入力データを取得
        val inputData = textInputEditTextDirectMessage.text.toString()
        if (inputData == ""){
            makeToast(MyApplication.appContext, getString(R.string.toast_message_no_input_message))
            return
        }

        //送信
        val service = setService()

        val itemContactObj = ItemContactSerializerModel(message=inputData, item=itemObj)
        service.postItemContactAPIView(sessionData.authTokenHeader!!, itemContactObj).enqueue(object :Callback<ResultModel>{


            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                //成功した場合には、入力内容を消去し、onResumeメソッドを実施
                println("onResponseを通る")
                val result = response.body()!!.result
                if (result == "success"){
                    makeToast(MyApplication.appContext, "メッセージが送信されました。")
                    textInputEditTextDirectMessage.setText("")
                    this@ItemContactFragment.onResume()
                }
            }

            override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                println("onFailureを通る")
                println(t)
                println(t.message)
            }
        })
    }


    fun setUpRecyclerView(itemContactObjects: ArrayList<ItemContactSerializerModel>){

        //RecyclerViewを画面に描画する
        val layoutManager = LinearLayoutManager(MyApplication.appContext)
        recyclerViewDirectMessage.layoutManager = layoutManager.apply {
            stackFromEnd = true
        }
        recyclerViewDirectMessage.adapter = MyItemContactRecyclerViewAdapter(itemContactObjects)
    }



}
