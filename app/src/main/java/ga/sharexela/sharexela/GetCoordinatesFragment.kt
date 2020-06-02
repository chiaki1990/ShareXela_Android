package ga.sharexela.sharexela

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_get_coordinates.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




class GetCoordinatesFragment : Fragment(), OnMapReadyCallback {

    private var itemObj: ItemSerializerModel? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    //var marker: Marker? = null
    //var circle: Circle? = null
    var profileObj: ProfileSerializerModel? = null
    var point: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemObj = it.getSerializable(ARG_PARAM1) as ItemSerializerModel?
            param2  = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_get_coordinates, container, false)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)


        menu.apply {
            findItem(R.id.menuSearch).isVisible = false
            findItem(R.id.menuGoHome).isVisible = false
            findItem(R.id.action_settings).isVisible = false
            findItem(R.id.menuDone).isVisible = true
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menuDone -> {
                //CrearArticuloActivityからの場合 -> CrearArticuloActivityにデータを引き渡す
                //ProfileActivityからの場合       -> Profileオブジェクトの更新のためHttp通信を実行

                println("反応するか")
                if (point == "") return true

                //記事新規作成の地理データを更新する
                if (parentFragmentManager.findFragmentByTag(FragmentTag.FROM_CREAR_ARTICULO_FRAGMENT.name) != null){
                    updateRegionDataByPoint(itemObj)
                }
                //記事編集の地理データを更新する
                if (parentFragmentManager.findFragmentByTag(FragmentTag.FROM_EDITAR_ARTICULO_FRAGMENT.name) != null){
                    updateRegionDataByPoint(itemObj)
                }

                //地理データを送信する
                if (parentFragmentManager.findFragmentByTag("fromEditAreaInfoFragment") != null){
                    if (profileObj == null) return true
                    sendGeoDataToProfile(profileObj!!)
                }
            }
        }
        return true
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //fragmentManager!!.beginTransaction().add(R.id.frameLayoutProfile, map).commit()
        val map = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().add(R.id.framelayoutForMapstest, map).commit()
        map.getMapAsync(this@GetCoordinatesFragment)
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

        fun sendCrearArticuloFragmentAgain(itemObj:ItemSerializerModel?)

        fun sendEditarArticuloFragmentAgain(itemObj:ItemSerializerModel?)
    }

    companion object {

        @JvmStatic
        fun newInstance(itemObj: ItemSerializerModel?, param2: String) =
            GetCoordinatesFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, itemObj)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onMapReady(googleMap: GoogleMap) {



        googleMap.uiSettings.isMapToolbarEnabled = false

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(14.845833, -91.518889), 12.0f))

        //CrearArticuloActivityから来た場合は位置情報を描画する
        googleMap.isMyLocationEnabled = true


        googleMap.setOnMapClickListener {

            // Pointテンプレート: "SRID=4326;POINT (-92.53475189208982(Lng) 16.7240018958819(Lat))"
            var pointFormat = "SRID=4326;POINT (%s %s)"
            point = pointFormat.format(it.longitude.toString(), it.latitude.toString())


            //まず入力数値を取得する
            var radiusLength = etRadiusLength.text.toString()
            if (radiusLength == ""){
                radiusLength = "0"

                drawingPoint(it, googleMap)


                //ここでProfileObjに変換するかItemObjに変換するかの分岐がほしい
                if (parentFragmentManager.findFragmentByTag("fromEditAreaInfoFragment") != null){
                    //ProfileSerializerModelオブジェクトを生成
                    profileObj = ProfileSerializerModel(point=point, radius=radiusLength.toInt())
                }
                if (parentFragmentManager.findFragmentByTag(FragmentTag.FROM_CREAR_ARTICULO_FRAGMENT.name) != null){
                    //ItemSerializerModelオブジェクト(itemObj)の更新
                    itemObj!!.point  = point
                    itemObj!!.radius = radiusLength.toInt()
                }
                if (parentFragmentManager.findFragmentByTag(FragmentTag.FROM_EDITAR_ARTICULO_FRAGMENT.name) != null){
                    //ItemSerializerModelオブジェクト(itemObj)の更新
                    itemObj!!.point  = point
                    itemObj!!.radius = radiusLength.toInt()
                }


            }
            drawingCircle(it, radiusLength, googleMap)

            //ここでProfileObjに変換するかItemObjに変換するかの分岐がほしい
            if (parentFragmentManager.findFragmentByTag("fromEditAreaInfoFragment") != null){
                //ProfileSerializerModelオブジェクトを生成
                profileObj = ProfileSerializerModel(point=point, radius=radiusLength.toInt())
            }
            if (parentFragmentManager.findFragmentByTag(FragmentTag.FROM_CREAR_ARTICULO_FRAGMENT.name) != null){
                //ItemSerializerModelオブジェクト(itemObj)の更新
                itemObj!!.point  = point
                itemObj!!.radius = radiusLength.toInt()
            }
            if (parentFragmentManager.findFragmentByTag(FragmentTag.FROM_EDITAR_ARTICULO_FRAGMENT.name) != null){
                //ItemSerializerModelオブジェクト(itemObj)の更新
                itemObj!!.point  = point
                itemObj!!.radius = radiusLength.toInt()
            }
        }
    }




    private fun sendGeoDataToProfile(profileObj:ProfileSerializerModel){
        //機能: pointをもとにadm1, adm2に変更を反映する(django:post_save(update_fields)を通じてpointに依拠したadm1,adm2に変更する)

        if (sessionData.authTokenHeader == null) return
        ServiceProfile.patchProfile(sessionData.authTokenHeader!!, profileObj, MyApplication.appContext)
        return
    }


    private fun backToCrearArticuloFragment(itemObj: ItemSerializerModel){

        listener!!.sendCrearArticuloFragmentAgain(itemObj)
    }

    private fun backToEditarArticuloFragment(itemObj: ItemSerializerModel){

        listener!!.sendEditarArticuloFragmentAgain(itemObj)
    }



    private fun updateRegionDataByPoint(itemObj: ItemSerializerModel?){

        if (sessionData.authTokenHeader == null) return

        //point値からadm1, adm2を取得
        val service = setService()
        service.postGetRegionDataByPointAPIView(sessionData.authTokenHeader, itemObj!!.point!!).enqueue(object :Callback<ResultRegionModel>{

            override fun onResponse(call: Call<ResultRegionModel>, response: Response<ResultRegionModel>) {
                println("onResponseを通過 :GetCoordinatesFragment#updateRegionDataByPoint")

                val adm1: String? = response.body()!!.adm1
                val adm2: String? = response.body()!!.adm2

                //itemObjのadm1,adm2を更新
                if (adm1 != null) itemObj.adm1 = adm1
                if (adm2 != null) itemObj.adm2 = adm2

                if (parentFragmentManager.findFragmentByTag(FragmentTag.FROM_CREAR_ARTICULO_FRAGMENT.name) != null) {
                    //CrearArticuloFragmentを再度開く
                    backToCrearArticuloFragment(itemObj)
                }else if(parentFragmentManager.findFragmentByTag(FragmentTag.FROM_EDITAR_ARTICULO_FRAGMENT.name) != null){
                    //EditarArticuloFragmentを再度開く
                    backToEditarArticuloFragment(itemObj)
                }

                //このフラグメントを閉じる
                parentFragmentManager.beginTransaction()
                    .remove(this@GetCoordinatesFragment)
                    .commit()

            }

            override fun onFailure(call: Call<ResultRegionModel>, t: Throwable) {
                println("onFailureを通過 :GetCoordinatesFragment#updateRegionDataByPoint")
                println(t)
                println(t.message)
            }
        })
    }


}





fun drawingPoint(latLng: LatLng, googleMap:GoogleMap) {
    googleMap.clear()
    googleMap.addMarker(MarkerOptions().position(LatLng(latLng.latitude, latLng.longitude)))
}


fun drawingCircle(latLng: LatLng, radiusLength: String, googleMap:GoogleMap){

    googleMap.clear()
    googleMap.addMarker(MarkerOptions().position(LatLng(latLng.latitude, latLng.longitude)))

    googleMap.addCircle(
            CircleOptions().
            center(LatLng(latLng.latitude, latLng.longitude ))
                .radius(radiusLength.toDouble())
                .strokeColor(Color.RED)
                .fillColor(0x220000FF))
}

fun getLatLng(itemObj: ItemSerializerModel):LatLng{
    val itemPoint = itemObj.point
    val lng = itemPoint!!.split(" ")[1].substring(1).toDouble()
    val lat_base = itemPoint.split(" ")[2]
    val lat = itemPoint.split(" ")[2].substring(0, lat_base.length-1).toDouble()
    return LatLng(lat, lng)
}


