package com.example.takayama

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_solicitar_decide.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"






class SolicitarDecideFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var selectedSolicitud: SolicitudSerializerModel? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedSolicitud = it.getSerializable(ARG_PARAM1) as SolicitudSerializerModel
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_solicitar_decide, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        println(selectedSolicitud)

        //Solicitudインスタンスのデータの取得
        val userName:String = selectedSolicitud!!.applicant!!.user!!.username!!
        val message:String? = selectedSolicitud!!.message
        val profileImage = selectedSolicitud!!.applicant!!.image
        val profileImageUrl = BASE_URL + profileImage!!.substring(1)

        //取得したデータを画面へ反映
        tvSolicitarUserNameDetail.text = userName
        tvSolicitarMessageDetail.text = message
        Glide.with(MyApplication.appContext).load(profileImageUrl).into(ivSolicitarImageDetail)

        //決定するボタンを押した場合のリスナーを設置
        btnDecideSolicitar.setOnClickListener {

            //いきなりDirectMessageActivityを開いても駄目で、一度SolicitudインスタンスのacceptedをTrueに変更する必要がある。
            val service = setService()
            val authTokenHeader = " Token " + authToken
            service.patchSolicitudAPIView(authTokenHeader=authTokenHeader, solicitudObjId=selectedSolicitud!!.id!!).enqueue(object :
                Callback<ResultModel>{

                override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                    println("onResponseを通る。")

                    if (!response.isSuccessful) return
                    val result = response.body()!!.result
                    if (result == "success" ){
                        //取引画面へ移動するアクティビティを起動するためにコールバックを実行する
                        //引数として渡すものを考えておく。おそらくItemSerializerModelを渡すのが良いと思われる
                        // selectedSolicitud.itemはItemSerializerModelとして使うことができる
                        val itemObj: ItemSerializerModel = selectedSolicitud!!.item!!
                        listener!!.launchDirectMessageActivity(itemObj)
                    }

                }

                override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                    println("onFailureを通る。")

                }
            })
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

        //DirectMessageActivityを起動するためのメソッド
        fun launchDirectMessageActivity(itemObj: ItemSerializerModel)

    }

    companion object {

        @JvmStatic
        fun newInstance(selectedSolicitud: SolicitudSerializerModel, param2: String) =
            SolicitarDecideFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, selectedSolicitud)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
