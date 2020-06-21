package ga.sharexela.sharexela

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_contact_us.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ContactFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ContactFragment.newInstance] factory method to
 * create an instance of this fragment.
 */




class ContactUsFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_contact_us, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val titleList = arrayListOf<String>(
            "Como registrarte e inicio de sesión",
            "Método de publicación / reglas de publicación",
            "Probemas entre usuarios",
            "Otros")

        var selectedTitle:String = "";

        spTitle.adapter = ArrayAdapter<String>(MyApplication.appContext, android.R.layout.simple_spinner_item, titleList)


        spTitle.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedTitle = titleList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //println("test")
                selectedTitle = titleList[0]
            }
        }


        btnContact.setOnClickListener{
            //各editTextのデータを取得
            val inputEmailAddress = etEmailAddress.text.toString()
            val inputContent = etContenido.text.toString()

            if (inputEmailAddress.length == 0){
                //emailAddressが入力されていない場合には、emailAddressの入力を促す。
                etEmailAddress.setText("EmailAddressが入力されていません。")
                etContenido.setTextColor(Color.RED)

            } else if (inputContent.length == 0) {
                //contentが入力されていない場合には、contentに入力を促す。
                etContenido.setText("Contenidoが入力されていません。")
                etContenido.setTextColor(Color.RED)

            }else{
                //各データをContactModel(DataClass)に変換する
                val contact = ContactSerializerModel(selectedTitle, inputEmailAddress, inputContent)
                createContactInstance(contact)
            }

        }


    }

    private fun createContactInstance(contact: ContactSerializerModel) {

        val service = setService()
        service.postContactInstance(sessionData.authTokenHeader, contact).enqueue(object : Callback<ResultModel>{

            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                println("ONRESPONSE" + response)
                val result:String = response.body()!!.result
                if (result == "success") {
                    //送信したらトーストの表示
                    makeToast(MyApplication.appContext, "質問が送信されました")

                    //コールバックしてfinish()を実行する
                    listener!!.successContactInstance()


                }else if (result == "fail"){
                    //失敗した旨を表示し再入力を促す
                    makeToast(MyApplication.appContext, "失敗しました。もう一度入力して下さい。")
                }
            }



            override fun onFailure(call: Call<ResultModel>, t: Throwable) {

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
        fun successContactInstance()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ContactFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactUsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onPause() {
        super.onPause()
        //キーボードが表示中なら削除する(HomeFragmentで画面描画されなくなる欠陥に対応)
        hideKeybord(this)
    }
}
