package ga.sharexela.sharexela

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_get_coordinates.*




// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"






class GetCoordinatesFragment : Fragment(), OnMapReadyCallback {

    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    var marker: Marker? = null
    var circle: Circle? = null
    var profileObj: ProfileSerializerModel? = null



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
                //ProfileActivityからの場合       -> Profileオブジェクトの更新のためHttp通信を実行
                //CrearArticuloActivityからの場合 -> CrearArticuloActivityにデータを引き渡す

                //地理データを送信する
                if (profileObj == null) return true
                sendGeoDataToProfile(profileObj!!)
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

    }

    companion object {


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GetCoordinatesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onMapReady(googleMap: GoogleMap?) {

        googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(14.845833, -91.518889), 12.0f))

        //CrearArticuloActivityから来た場合は位置情報を描画する
        googleMap.isMyLocationEnabled = true


        googleMap.setOnMapClickListener {

            // Pointテンプレート: "SRID=4326;POINT (-92.53475189208982(Lng) 16.7240018958819(Lat))"
            var pointFormat = "SRID=4326;POINT (%s %s)"
            var point = pointFormat.format(it.longitude.toString(), it.latitude.toString())


            //まず入力数値を取得する
            var radiusLength = etRadiusLength.text.toString()
            if (radiusLength == ""){
                radiusLength = "0"

                drawingPoint(it, googleMap)
                //ProfileSerializerModelオブジェクトを生成
                profileObj = ProfileSerializerModel(point=point, radius=radiusLength.toInt())

            }
            drawingCircle(it, radiusLength, googleMap)
            //ProfileSerializerModelオブジェクトを生成
            profileObj = ProfileSerializerModel(point=point, radius=radiusLength.toInt())

        }
    }


    private fun drawingPoint(latLng: LatLng, googleMap:GoogleMap){
        if (marker == null){
            marker = googleMap.addMarker(MarkerOptions()
                .position(LatLng(latLng.latitude, latLng.longitude)))
        }else if (marker != null){
            marker!!.remove()
            marker = googleMap.addMarker(MarkerOptions()
                .position(LatLng(latLng.latitude, latLng.longitude)))
        }
        return
    }

    private fun drawingCircle(latLng: LatLng, radiusLength: String, googleMap:GoogleMap){

        if (circle == null){

            marker = googleMap.addMarker(MarkerOptions()
                .position(LatLng(latLng.latitude, latLng.longitude)))

            circle = googleMap.addCircle(
                CircleOptions().
                center(LatLng(latLng.latitude, latLng.longitude ))
                    .radius(radiusLength.toDouble())
                    .strokeColor(Color.RED)
                    .fillColor(0x220000FF))

        }else if (circle != null){

            marker!!.remove()
            marker = googleMap.addMarker(MarkerOptions()
                .position(LatLng(latLng.latitude, latLng.longitude)))

            circle!!.remove()
            circle = googleMap.addCircle(
                CircleOptions().
                center(LatLng(latLng.latitude, latLng.longitude ))
                    .radius(radiusLength.toDouble())
                    .strokeColor(Color.RED)
                    .fillColor(0x220000FF))
        }
        return
    }








    private fun sendGeoDataToProfile(profileObj:ProfileSerializerModel){

        //val service = setService()
        //service.patchProfile(sessionData.authTokenHeader!!, profileObj)

        if (sessionData.authTokenHeader == null) return
        ServiceProfile.patchProfile(sessionData.authTokenHeader!!, profileObj, MyApplication.appContext)
        return
    }


    private fun backCrearArticuloFragment(){
        //何がしたいか？画面の復元と取得したデータの反映
        //取得したデータ
        var radiusLength = etRadiusLength.text.toString()
        if (radiusLength == "") {
            radiusLength = "0"
        }


    }


}
