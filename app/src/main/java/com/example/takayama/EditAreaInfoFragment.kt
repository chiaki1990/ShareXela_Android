package com.example.takayama

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_edit_area_info.*
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
 * [EditAreaInfoFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [EditAreaInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */




class EditAreaInfoFragment : Fragment() {


    lateinit var paisItems:ArrayList<String>;
    lateinit var departamentoItems:ArrayList<String>;
    lateinit var municipioItems:ArrayList<String>;
    var selectedPaisPosition:Int = 0;
    var selectedDepartamentoPosition:Int = 0;
    var selectedMunicipioPosition:Int = 0;




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
        return inflater.inflate(R.layout.fragment_edit_area_info, container, false)
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



        //SPINNERにリスナーセット
        spPais.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //選択されたアイテムをPaisにする
                selectedPaisPosition = position
                println("SPINNERのPAISがセットされました。")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        spDepartamento.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //選択されたアイテムをDepartamentoにする
                selectedDepartamentoPosition = position
                println("SPINNERのDEPARTAMENTOがセットされました。")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }


        spMunicipio.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //選択されたアイテムをMunicipioにする
                selectedMunicipioPosition = position
                println("SPINNERのMUNICIPIOがセットされました。")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }


        btnAreaInfo.setOnClickListener {
            //選択したスピナーからデータを取得

            //ProfileModelオブジェクトの作成
            var profile = ProfileSerializerModel()
            profile.adm0 = paisItems[selectedPaisPosition]
            profile.adm1 = departamentoItems[selectedDepartamentoPosition]
            profile.adm2 = municipioItems[selectedMunicipioPosition]

            //retrofitで修正内容を送信
            val authTokenHeader = " Token " + authToken
            ServiceProfile.patchProfile(authTokenHeader!!, profile, MyApplication.appContext)
        }


        doGetRegion()

    }




    override fun onResume() {
        super.onResume()
        //現在のPAIS、DEPARTAMENTO,MUNIIPIOをSPINNERにセットする。

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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditAreaInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditAreaInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    private fun doGetRegion() {

        val service = setService()
        service.getRegionList().enqueue(object : Callback<RegionListSet> {

            override fun onResponse(call: Call<RegionListSet>, response: Response<RegionListSet>) {

                //responseを解析
                val adm0_list = response.body()?.ADM0_LIST
                paisItems = adm0_list!!

                val adm1_list = response.body()?.ADM1_LIST
                departamentoItems = adm1_list!!

                val adm2_list = response.body()?.ADM2_LIST
                municipioItems = adm2_list!!

                //SPINNERにadapterを通じて反映させる。
                spPais.adapter = ArrayAdapter<String>(
                    MyApplication.appContext,
                    android.R.layout.simple_spinner_item,
                    paisItems)

                var paisIndex = paisItems.indexOf(adm0)
                spPais.setSelection(paisIndex)

                spDepartamento.adapter = ArrayAdapter<String>(
                    MyApplication.appContext,
                    android.R.layout.simple_spinner_item,
                    departamentoItems)

                var departamentoIndex = departamentoItems.indexOf(adm1)
                spDepartamento.setSelection(departamentoIndex)

                spMunicipio.adapter = ArrayAdapter<String>(
                    MyApplication.appContext,
                    android.R.layout.simple_spinner_item,
                    municipioItems)

                var municipioIndex = municipioItems.indexOf(adm2)
                spMunicipio.setSelection(municipioIndex)



            }

            override fun onFailure(call: Call<RegionListSet>, t: Throwable) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

    }

}

