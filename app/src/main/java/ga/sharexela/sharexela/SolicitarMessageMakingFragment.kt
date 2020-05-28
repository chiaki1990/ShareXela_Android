package ga.sharexela.sharexela

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_solicitar_message_making.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response





// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




class SolicitarMessageMakingFragment : Fragment() {
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



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_solicitar_message_making, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        btnSolicitarMakingMessage.setOnClickListener {
            //データを取得する
            val message: String = textInputEditTextSolicitarMessage.text.toString()

            //データを送信し、Solicitudインスタンスを生成する
            val service = setService()

            val solicitudObj = SolicitudSerializerModel(message=message)

            service.postSolicitudAPIView(sessionData.authTokenHeader, solicitudObj, itemObj!!.id!!).enqueue(object : Callback<ResultModel>{

                override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                    println("onResponseを通る。　SolicitarMessageMakingFragment#onActivityCreated")

                    val result = response.body()!!.result
                    if (result == "success"){
                        //インスタンスの生成が完了 -> Toast表示を行う
                        makeToast(MyApplication.appContext, "申請しました。")

                        listener!!.finishSolicitarActivity()

                    }
                }

                override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                    println("onFailureを通る。　SolicitarMessageMakingFragment#onActivityCreated")
                    println(t)
                    println(t.message)
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
        fun finishSolicitarActivity()
    }



    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(itemObj: ItemSerializerModel, param2: String) =
            SolicitarMessageMakingFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, itemObj)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
