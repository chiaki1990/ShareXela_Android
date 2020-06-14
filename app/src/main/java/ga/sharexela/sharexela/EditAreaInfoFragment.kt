package ga.sharexela.sharexela

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import com.google.maps.android.data.geojson.GeoJsonLayer
import kotlinx.android.synthetic.main.fragment_edit_area_info.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.Permission


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"





class EditAreaInfoFragment : Fragment(), OnMapReadyCallback {

    //var paisItems:Array<String> = MyApplication.appContext.resources.getStringArray(R.array.paisList) //as ArrayList<String>
    //var departamentoItems:Array<String> = MyApplication.appContext.resources.getStringArray(R.array.departamentoList)
    //var municipioItems:Array<String> = MyApplication.appContext.resources.getStringArray(R.array.municipioList)


    //lateinit var map: SupportMapFragment
    //lateinit var geoJsonData: JSONObject;
    //lateinit var muniGeoJson: JSONObject;



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




        btnSettingAreaByMap.setOnClickListener {
            //permissionsのチェックを行う
            var permissionStatus = isAllPermissionsGranted(REQUIRED_PERMISSIONS_LOCATION)
            if (permissionStatus == true) return@setOnClickListener showMap()
            val neverAskAgainSelectedStatus = PermissionUtils().neverAskAgainSelected(REQUIRED_PERMISSIONS_LOCATION[0], this)
            if (!neverAskAgainSelectedStatus ) return@setOnClickListener this.requestPermissions(REQUIRED_PERMISSIONS_LOCATION, REQUEST_CODE_PERMISSIONS_LOCATION)
            if (neverAskAgainSelectedStatus  ) return@setOnClickListener displayNeverAskAgainDialog(this)
        }


        btnAreaInfo.setOnClickListener {
            //選択したスピナーからデータを取得

            //ProfileModelオブジェクトの作成
            var profileObjForSend  = ProfileSerializerModel()
            profileObjForSend.adm0 = spPais.selectedItem.toString()
            profileObjForSend.adm1 = spDepartamento.selectedItem.toString()
            profileObjForSend.adm2 = spMunicipio.selectedItem.toString()

            //retrofitで修正内容を送信
            ServiceProfile.patchProfile(sessionData.authTokenHeader!!, profileObjForSend, MyApplication.appContext)
        }

    }


    override fun onResume() {
        super.onResume()

        if (sessionData.logInStatus == true) {
            setValueToPaisSpinner(sessionData.profileObj!!.adm0!!, spPais)
            setValueToDepartamentoSpinner(sessionData.profileObj!!.adm1!!, spDepartamento)
            setValueToMunicipioSpinner(sessionData.profileObj!!.adm2!!, spMunicipio)
        }
        //GoogleMapsにエリアを描画
        if (sessionData.profileObj!!.point != null){
            describeAreaInMap()
        }
        //Spinnerに地域リストのセット&ユーザー地域をセット
        setRegionData()

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

        fun launchGetCoordinatesFragment()

    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditAreaInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }




    fun setRegionData(){

        //SPINNERにadapterをセット
        setAdapterToPaisSpinner(spPais)
        setAdapterToDepartamentoSpinner(spDepartamento)
        setAdapterToMunicipioSpinner(spMunicipio)


        if (parentFragmentManager.findFragmentByTag(FragmentTag.PROFILE_EDIT_AREA_CHANGE.name) !=null) {

            setValueToPaisSpinner(sessionData.profileObj!!.adm0!!, spPais)
            setValueToDepartamentoSpinner(sessionData.profileObj!!.adm1!!, spDepartamento)
            setValueToMunicipioSpinner(sessionData.profileObj!!.adm2!!, spMunicipio)

        }else if (parentFragmentManager.findFragmentByTag(FragmentTag.PROFILE_EDIT_AREA_NEW.name) !=null){

            setValueToPaisSpinner("Guatemala", spPais)
            setValueToDepartamentoSpinner("Quetzaltenango", spDepartamento)
            setValueToMunicipioSpinner("Quetzaltenango", spMunicipio)

        }

    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != REQUEST_CODE_PERMISSIONS_LOCATION) return

        // requestCode == REQUEST_CODE_PERMISSIONS_LOCATIONの場合
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED } ) {
            println("パーミッションのここ2")
            showMap()
            return
        }else{
            makeToast(MyApplication.appContext, "Permissions not granted by the user.")
            PermissionUtils().setShouldShowStatus(permissions[0])
            return
        }

    }

    fun showMap(){
        listener!!.launchGetCoordinatesFragment()
    }

    fun describeAreaInMap(){
        val map = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().add(R.id.framelayoutEditAreaInfoMap, map).commit()
        map.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {

        //描画したいこと mapの縮尺, mapの中心設定, pointまたはradiusの描画
        //mapの縮尺
        if (sessionData.profileObj!!.point == null) return
        val latLng = getLatLng(sessionData.profileObj!!.point!!)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f))
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isZoomGesturesEnabled = true

        if (sessionData.profileObj!!.radius != 0){
            drawingCircle(latLng, sessionData.profileObj!!.radius.toString(), googleMap)
        }else if (sessionData.profileObj!!.radius == 0){
            drawingPoint(latLng, googleMap)
        }
    }

}

