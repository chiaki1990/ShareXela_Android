package ga.sharexela.sharexela

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_crear_articulo.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




class CrearArticuloFragment : Fragment(), OnMapReadyCallback {

    private var itemObj: ItemSerializerModel? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemObj = it.getSerializable(ARG_PARAM1) as ItemSerializerModel?
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crear_articulo, container, false)
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        //spinner(Articuloの記事カテゴリ)の選択肢をセットする
        setAdapterToCategotySpinner(spArticuloCategory)


        //具体的な座標データ取得するためにGoogleMapsを開く。このきのうのためにパーミションチェックを実装
        btnCrearArticuloLaunchMap.setOnClickListener {

            // 位置情報のパーミッション状態を取得する
            var permissionStatus = isAllPermissionsGranted(REQUIRED_PERMISSIONS_LOCATION)
            if (permissionStatus == true) return@setOnClickListener showMap()
            // パーミッション許可をリクエストし、許可が出ればshowMap()が実行される
            val neverAskAgainSelectedStatus = PermissionUtils().neverAskAgainSelected(REQUIRED_PERMISSIONS_LOCATION[0], this)
            if (!neverAskAgainSelectedStatus ) return@setOnClickListener this.requestPermissions(REQUIRED_PERMISSIONS_LOCATION, REQUEST_CODE_PERMISSIONS_LOCATION)
            if (neverAskAgainSelectedStatus  ) return@setOnClickListener displayNeverAskAgainDialog(this)
        }



        //btnCrearArticuloのリスナーセット
        btnCrearArticulo.setOnClickListener {
            //サーバーにデータ送信
            postCrearArticuloData()
        }

        //画像をタップするとギャラリーからデータを引っ張る checkImagePermissionsにリファクタリング
        ivArticuloImage1.setOnClickListener { checkImagePermissions() }
        ivArticuloImage2.setOnClickListener { checkImagePermissions() }
        ivArticuloImage3.setOnClickListener { checkImagePermissions() }


        //Regionデータをstring.xmlから取得してRegionをセット
        setRegionSpinner(itemObj, spSelectPais, spSelectDepartamento, spSelectMunicipio)

    }




    override fun onResume() {

        super.onResume()
        if (itemObj == null) return

        //画面に再反映させる
        setValueToCategotySpinner(itemObj!!, spArticuloCategory)
        etArticuloTitle.setText(itemObj!!.title)
        etArticuloDescription.setText(itemObj!!.description)
        if (itemObj!!.price != null) etArticuloPrice.setText(Integer.toString(itemObj!!.price!!))
        tvCrearArticuloPoint.text = itemObj?.point
        tvCrearArticuloRadius.text = itemObj!!.radius.toString()
        setRegionSpinner(itemObj, spSelectPais, spSelectDepartamento, spSelectMunicipio)

        if (imageView1FilePath != null && imageView1FilePath != "") Glide.with(this).load(imageView1FilePath).into(ivArticuloImage1)
        if (imageView2FilePath != null && imageView2FilePath != "") Glide.with(this).load(imageView2FilePath).into(ivArticuloImage2)
        if (imageView3FilePath != null && imageView3FilePath != "") Glide.with(this).load(imageView3FilePath).into(ivArticuloImage3)

        //pointがある場合にはgoogleMapsに描画する
        //frameLayoutCrearMap.visibility = View.GONE
        if (itemObj?.point != null) {
            frameLayoutCrearMap.visibility = View.VISIBLE
            val map = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction().add(R.id.frameLayoutCrearMap, map)
                .commit()
            map.getMapAsync(this@CrearArticuloFragment)
        }

    }


    private fun showMap() {

        //データをとる関数を実施
        val itemObj = retrieveArticuloData(
                etArticuloTitle, etArticuloDescription, etArticuloPrice, spArticuloCategory,
                spSelectPais, spSelectDepartamento,spSelectMunicipio,
                tvCrearArticuloPoint, tvCrearArticuloRadius)

        //もしデータが必要ならこのitemObjを使用する
        listener!!.launchGetCoordinatesFragment(itemObj, FragmentTag.FROM_CREAR_ARTICULO_FRAGMENT.name)
    }



    private fun postCrearArticuloData() {


        //入力データの取得
        val title = etArticuloTitle.text.toString()
        val description = etArticuloDescription.text.toString()


        //タイトルや説明欄に入力データデータがないときにメッセージを表示する(バリデーション)
        if (title == "" || description == "") {
            makeToast(MyApplication.appContext, getString(R.string.title_o_description_blank_message))
            return
        }

        //プログレスバーを表示
        progressBar.visibility = View.VISIBLE


        val retrievedItemObj = retrieveArticuloData(
                etArticuloTitle, etArticuloDescription, etArticuloPrice, spArticuloCategory,
                spSelectPais, spSelectDepartamento,spSelectMunicipio,
                tvCrearArticuloPoint, tvCrearArticuloRadius)


        //ivArticuloImageにセットされたuriをMultipartBody.Partオブジェクトに変換する
        var part1 = makeImgagePartForRetrofit(imageView1FilePath, IMAGE1)
        var part2 = makeImgagePartForRetrofit(imageView2FilePath, IMAGE2)
        var part3 = makeImgagePartForRetrofit(imageView3FilePath, IMAGE3)

        val reqBody :RequestBody = RequestBody.create(MediaType.parse("application/json"), Gson().toJson(retrievedItemObj))


        val service = setService()

        service.postItemCreateAPIViewMultiPart(authTokenHeader= sessionData.authTokenHeader!!, file1=part1, file2 = part2, file3 = part3, requestBody=reqBody).enqueue(object :Callback<ResultModel>{
            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                progressBar.visibility = View.GONE
                println("onResponseを通る : CrearArticuloFragment#postItemCreateAPIViewMultiPart")
                println(call.request().body())


                //CrearActivityを切る
                listener!!.successCrearArticulo()
            }

            override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                println("onFailureを通る : CrearArticuloFragment#postItemCreateAPIViewMultiPart")
                println(call.request().body())
                println(t)
                println(t.message)
                progressBar.visibility = View.GONE
                makeToast(MyApplication.appContext, getString(R.string.fail_crear_articulo))
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



    fun checkImagePermissions(){
        //パーミッションのチェック
        val permissionStatus = isAllPermissionsGranted(REQUIRED_PERMISSIONS_IMAGES)

        //大丈夫ならアクティビティを起動する準備を行う
        if (permissionStatus == true) return listener!!.onLaunchImagesActivity()
        //パーミッションが取れていないならdon't ask againに該当するかチェック
        for (permission in REQUIRED_PERMISSIONS_IMAGES){
            val neverAskAgainSelectedStatus = PermissionUtils().neverAskAgainSelected(permission, this)
            if (neverAskAgainSelectedStatus) return displayNeverAskAgainDialog(this)
        }
        return this.requestPermissions(REQUIRED_PERMISSIONS_IMAGES, REQUEST_CODE_PERMISSIONS_IMAGES)
    }


    interface OnFragmentInteractionListener {

        //CrearArticuloActivityに戻し、Activityをfinish()する
        fun successCrearArticulo()

        //画像をImageViewにセットするためのImagesActivityを起動する
        fun onLaunchImagesActivity()

        //取引エリア、地点を取得するフラグメントを起動する
        fun launchGetCoordinatesFragment(itemObj: ItemSerializerModel, launchFrom: String)

    }


    companion object {

        @JvmStatic
        fun newInstance(itemObj: ItemSerializerModel?, param2: String) =
            CrearArticuloFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, itemObj)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        println("パーミッションのここ")
        println(requestCode.toString())
        if (requestCode != REQUEST_CODE_PERMISSIONS_LOCATION || requestCode != REQUEST_CODE_PERMISSIONS_IMAGES ) return

        // requestCode == REQUEST_CODE_PERMISSIONS_LOCATIONの場合
        if (requestCode == REQUEST_CODE_PERMISSIONS_LOCATION) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED } ){
                println("パーミッションのここ2")
                showMap()
                return
            }else{
                makeToast(MyApplication.appContext, "Permissions not granted by the user.")
                PermissionUtils().setShouldShowStatus(permissions[0])
                return
            }

        }else if (requestCode == REQUEST_CODE_PERMISSIONS_IMAGES){
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED } ){
                listener!!.onLaunchImagesActivity()
            } else{
                makeToast(MyApplication.appContext, "Permissions not granted by the user.")
                for (permission in permissions){
                    PermissionUtils().setShouldShowStatus(permission)
                }
                return
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //描画したいこと mapの縮尺, mapの中心設定, pointまたはradiusの描画
        //mapの縮尺
        if (itemObj?.point == null) return
        val latLng = getLatLng(itemObj!!.point!!)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f))
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isZoomGesturesEnabled = true

        if (itemObj!!.radius != 0){
            drawingCircle(latLng, itemObj!!.radius.toString(), googleMap)
        }else if (itemObj!!.radius == 0){
            drawingPoint(latLng, googleMap)
        }
    }

}
