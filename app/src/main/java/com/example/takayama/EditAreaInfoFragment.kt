package com.example.takayama

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonParser
import com.google.maps.android.data.geojson.GeoJsonFeature
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle
import kotlinx.android.synthetic.main.fragment_edit_area_info.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"





class EditAreaInfoFragment : Fragment(), OnMapReadyCallback {


    lateinit var paisItems:ArrayList<String>;
    lateinit var departamentoItems:ArrayList<String>;
    lateinit var municipioItems:ArrayList<String>;
    var selectedPaisPosition:Int = 0;
    var selectedDepartamentoPosition:Int = 0;
    var selectedMunicipioPosition:Int = 0;


    lateinit var map: SupportMapFragment
    lateinit var geoJsonData: JSONObject;
    lateinit var muniGeoJson: JSONObject;




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

        println("profileObjのプリントEditAreaFra")
        println(sessionData.profileObj)

        map = SupportMapFragment.newInstance()


        //val fragmentTag = fragmentManager!!.findFragmentByTag(FragmentTag.PROFILE_EDIT_AREA_NEW.name)
    }




    override fun onResume() {
        super.onResume()


        //Pais選択のSPINNERにリスナーセット
        spPais.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //選択されたアイテムをPaisにする
                selectedPaisPosition = position
                println("SPINNERのPAISがセットされました。")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        //Departamento選択のSPINNERにリスナーセット
        spDepartamento.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //選択されたアイテムをDepartamentoにする
                selectedDepartamentoPosition = position
                println("SPINNERのDEPARTAMENTOがセットされました。")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        //Municipio選択のSPINNERにリスナーセット
        spMunicipio.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //選択されたアイテムをMunicipioにする
                selectedMunicipioPosition = position
                println("SPINNERのMUNICIPIOがセットされました。")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }


        btnAreaInfo.setOnClickListener {
            //選択したスピナーからデータを取得

            //ProfileModelオブジェクトの作成
            var profileObjForSend = ProfileSerializerModel()
            profileObjForSend.adm0 = paisItems[selectedPaisPosition]
            profileObjForSend.adm1 = departamentoItems[selectedDepartamentoPosition]
            profileObjForSend.adm2 = municipioItems[selectedMunicipioPosition]

            //retrofitで修正内容を送信

            ServiceProfile.patchProfile(sessionData.authTokenHeader!!, profileObjForSend, MyApplication.appContext)

            //地図情報を削除する
            // map = SupportMapFragment.newInstance()
            //fragmentManager!!.beginTransaction().add(R.id.framelayoutForMaps, map).commit()
            fragmentManager!!.beginTransaction().remove(map).commit()


        }



        doGetRegion()

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
        service.getRegionList(sessionData.authTokenHeader!!).enqueue(object : Callback<RegionListSet> {

            override fun onResponse(call: Call<RegionListSet>, response: Response<RegionListSet>) {
                println("onResponseを通る")

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


                if (fragmentManager!!.findFragmentByTag(FragmentTag.PROFILE_EDIT_AREA_CHANGE.name) !=null) {
                    var paisOfIndex = paisItems.indexOf(sessionData.profileObj!!.adm0)
                    spPais.setSelection(paisOfIndex)
                }else if (fragmentManager!!.findFragmentByTag(FragmentTag.PROFILE_EDIT_AREA_NEW.name) !=null){
                    var paisOfIndex = paisItems.indexOf("Guatemala")
                    spPais.setSelection(paisOfIndex)
                }



                spDepartamento.adapter = ArrayAdapter<String>(
                    MyApplication.appContext,
                    android.R.layout.simple_spinner_item,
                    departamentoItems)

                if (fragmentManager!!.findFragmentByTag(FragmentTag.PROFILE_EDIT_AREA_CHANGE.name) != null){
                    var departamentoOfIndex = departamentoItems.indexOf(sessionData.profileObj!!.adm1)
                    spDepartamento.setSelection(departamentoOfIndex)
                }else if (fragmentManager!!.findFragmentByTag(FragmentTag.PROFILE_EDIT_AREA_NEW.name) != null){
                        var departamentoOfIndex = departamentoItems.indexOf("Quetzaltenango")
                        spDepartamento.setSelection(departamentoOfIndex)
                    }



                spMunicipio.adapter = ArrayAdapter<String>(
                    MyApplication.appContext,
                    android.R.layout.simple_spinner_item,
                    municipioItems)


                if (fragmentManager!!.findFragmentByTag(FragmentTag.PROFILE_EDIT_AREA_CHANGE.name) != null){
                    var municipioOfIndex = municipioItems.indexOf(sessionData.profileObj!!.adm2)
                    spMunicipio.setSelection(municipioOfIndex)
                }else if (fragmentManager!!.findFragmentByTag(FragmentTag.PROFILE_EDIT_AREA_NEW.name) != null){
                    var municipioOfIndex = municipioItems.indexOf("Quetzaltenango")
                    spMunicipio.setSelection(municipioOfIndex)
                }


                /*

                val geoJson = response.body()?.geoJsonData!!
                val muniStrGeoJson = response.body()?.muniGeoJson
                geoJsonData = JSONObject(geoJson)
                muniGeoJson = JSONObject(muniStrGeoJson)

                //val map = SupportMapFragment.newInstance()
                fragmentManager!!.beginTransaction().add(R.id.framelayoutForMaps, map).commit()
                map.getMapAsync(this@EditAreaInfoFragment)


                 */

            }

            override fun onFailure(call: Call<RegionListSet>, t: Throwable) {
                println("onFailureを通過する")
                println(t)
                println(t.message)
            }
        })
    }



    private fun doGetRegionSetDefault() {
        //ユーザー登録されたユーザーに

        val service = setService()
        service.getRegionList(sessionData.authTokenHeader!!).enqueue(object : Callback<RegionListSet> {

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

                var paisIndex = paisItems.indexOf(sessionData.profileObj!!.adm0)
                spPais.setSelection(paisIndex)

                spDepartamento.adapter = ArrayAdapter<String>(
                    MyApplication.appContext,
                    android.R.layout.simple_spinner_item,
                    departamentoItems)

                var departamentoIndex = departamentoItems.indexOf(sessionData.profileObj!!.adm1)
                spDepartamento.setSelection(departamentoIndex)

                spMunicipio.adapter = ArrayAdapter<String>(
                    MyApplication.appContext,
                    android.R.layout.simple_spinner_item,
                    municipioItems)

                var municipioIndex = municipioItems.indexOf(sessionData.profileObj!!.adm2)
                spMunicipio.setSelection(municipioIndex)




                val geoJson = response.body()?.geoJsonData!!
                val muniStrGeoJson = response.body()?.muniGeoJson
                geoJsonData = JSONObject(geoJson)
                muniGeoJson = JSONObject(muniStrGeoJson)

                //val map = SupportMapFragment.newInstance()
                fragmentManager!!.beginTransaction().add(R.id.framelayoutForMaps, map).commit()
                map.getMapAsync(this@EditAreaInfoFragment)


            }

            override fun onFailure(call: Call<RegionListSet>, t: Throwable) {
                println("onFailureを通過する")
                println(t)
                println(t.message)
            }
        })
    }



    override fun onMapReady(googleMap: GoogleMap?) {
        //データを受け取ってから描画する

        googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(14.845833, -91.518889), 7.0f))

        val depLayer = GeoJsonLayer(googleMap, geoJsonData);

        depLayer.defaultPolygonStyle.fillColor = Color.GREEN

        depLayer.addLayerToMap()

        val muniLayer = GeoJsonLayer(googleMap, muniGeoJson)
        muniLayer.addLayerToMap()

    }


}

