package ga.sharexela.sharexela

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
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
import retrofit2.http.Header
import retrofit2.http.Part
import retrofit2.http.Path


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



class EditarArticuloFragment : Fragment(), OnMapReadyCallback {

    private var itemObj: ItemSerializerModel? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null


    //val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    //val REQUEST_CODE_PERMISSIONS = 15



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemObj = it.getSerializable(ARG_PARAM1) as ItemSerializerModel
            param2 = it.getString(ARG_PARAM2)
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

        //取引エリア、地点を取得するフラグメントを起動する
        fun launchGetCoordinatesFragment(itemObj: ItemSerializerModel, launchFrom: String)

        fun onLaunchImagesActivity()

        fun successCrearArticulo()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_crear_articulo, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //super.onCreateOptionsMenu(menu, inflater)
        menu.apply {
            findItem(R.id.menuSearch).isVisible = false
            findItem(R.id.menuGoHome).isVisible = false
            findItem(R.id.action_settings).isVisible = false
            findItem(R.id.menuDone).isVisible = false
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //itemObjのデータを反映させる
        applyItemObjData()

        //CREAR_ARTICULOレイアウトのおかしいwidgetを修正する
        modifyWidget()

        //具体的な座標データを利用する
        btnCrearArticuloLaunchMap.setOnClickListener{
            //位置情報のパーミッション状態を取得する
            //var permissionStatus = isAllPermissionsGranted(REQUIRED_PERMISSIONS)
            //if (permissionStatus == true) return@setOnClickListener listener!!.launchGetCoordinatesFragment(itemObj!!, FragmentTag.FROM_EDITAR_ARTICULO_FRAGMENT.name)
            // パーミッション許可をリクエストし、許可が出ればshowMap()が実行される
            //requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)


            // 位置情報のパーミッション状態を取得する
            var permissionStatus = isAllPermissionsGranted(REQUIRED_PERMISSIONS_LOCATION)
            if (permissionStatus == true) return@setOnClickListener listener!!.launchGetCoordinatesFragment(itemObj!!, FragmentTag.FROM_EDITAR_ARTICULO_FRAGMENT.name)
            // パーミッション許可をリクエストし、許可が出ればshowMap()が実行される
            val neverAskAgainSelectedStatus = PermissionUtils().neverAskAgainSelected(REQUIRED_PERMISSIONS_LOCATION[0], this)
            if (!neverAskAgainSelectedStatus ) return@setOnClickListener this.requestPermissions(REQUIRED_PERMISSIONS_LOCATION, REQUEST_CODE_PERMISSIONS_LOCATION)
            //if (neverAskAgainSelectedStatus  ) return@setOnClickListener requestPermissionsByDialog(REQUIRED_PERMISSIONS_LOCATION, REQUEST_CODE_PERMISSIONS_LOCATION, this, "map機能", "map機能を実現するには許可が必要です。" )
            if (neverAskAgainSelectedStatus  ) return@setOnClickListener displayNeverAskAgainDialog(this)

        }

        //btnCrearArticuloのリスナーセット
        btnCrearArticulo.setOnClickListener {
            //サーバーにデータ送信
            patchArticuloData()
        }


        //画像をタップするとギャラリーからデータを引っ張る
        ivArticuloImage1.setOnClickListener { listener!!.onLaunchImagesActivity() }
        ivArticuloImage2.setOnClickListener { listener!!.onLaunchImagesActivity() }
        ivArticuloImage3.setOnClickListener { listener!!.onLaunchImagesActivity() }

    }


    override fun onResume() {
        super.onResume()

        if (itemObj == null) return

        //画面に再反映させる
        setValueToCategotySpinner(itemObj!!, spArticuloCategory)
        etArticuloTitle.setText(itemObj!!.title)
        etArticuloDescription.setText(itemObj!!.description)
        if (itemObj!!.price != null) etArticuloPrice.setText(itemObj!!.price!!)
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
            map.getMapAsync(this@EditarArticuloFragment)
        }
    }



    companion object {


        @JvmStatic
        fun newInstance(itemObj: ItemSerializerModel, param2: String) =
            EditarArticuloFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, itemObj)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun applyItemObjData(){

        //修正項目: カテゴリ, 記事タイトル, 国, デパルタメント, ムニシピオ, 座標点, 取引半径(m), 画像1, 画像2, 画像3, 記事説明
        //spArticuloCategory
        setAdapterToCategotySpinner(spArticuloCategory)
        setValueToCategotySpinner(itemObj!!, spArticuloCategory)

        //etArticuloTitle
        etArticuloTitle.setText(itemObj!!.title)

        //spSelectPais
        setAdapterToPaisSpinner(spSelectPais)
        setValueToPaisSpinner(itemObj!!.adm0!!, spSelectPais)

        //spSelectDepartamento
        setAdapterToDepartamentoSpinner(spSelectDepartamento)
        setValueToDepartamentoSpinner(itemObj!!.adm1!!, spSelectDepartamento)

        //spSelectMunicipio
        setAdapterToMunicipioSpinner(spSelectMunicipio)
        setValueToMunicipioSpinner(itemObj!!.adm2!!, spSelectMunicipio)

        //tvCrearArticuloPoint
        tvCrearArticuloPoint.text = itemObj!!.point
        //tvCrearArticuloRadius
        tvCrearArticuloRadius.text = itemObj!!.radius.toString()
        //ivArticuloImage1
        if (itemObj!!.image1 != null) Glide.with(this).load(BASE_URL+itemObj!!.image1!!.substring(1)).into(ivArticuloImage1)
        //ivArticuloImage2
        if (itemObj!!.image2 != null) Glide.with(this).load(BASE_URL+itemObj!!.image2!!.substring(1)).into(ivArticuloImage2)
        //ivArticuloImage3
        if (itemObj!!.image3 != null) Glide.with(this).load(BASE_URL+itemObj!!.image3!!.substring(1)).into(ivArticuloImage3)
        //etArticuloDescription
        etArticuloDescription.setText(itemObj!!.description)

    }

    private fun modifyWidget(){
        //おかしい表示は修正する。おかしい点は？
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == REQUEST_CODE_PERMISSIONS_LOCATION) {

            if (isAllPermissionsGranted(REQUIRED_PERMISSIONS_LOCATION) != true) {

                makeToast(MyApplication.appContext, "Permissions not granted by the user.")
                return
            }

            listener!!.launchGetCoordinatesFragment(itemObj!!, FragmentTag.FROM_EDITAR_ARTICULO_FRAGMENT.name)
        }
    }

    fun patchArticuloData(){

        //入力データの取得
        val title = etArticuloTitle.text.toString()
        val description = etArticuloDescription.text.toString()


        //タイトルや説明欄に入力データデータがないときにメッセージを表示する(バリデーション)
        if (title == "" || description == "") {
            makeToast(MyApplication.appContext, getString(R.string.title_o_description_blank_message))
            return
        }

        progressBar.visibility = View.VISIBLE

        val retrievedItemObj = retrieveArticuloData(
            etArticuloTitle, etArticuloDescription, etArticuloPrice, spArticuloCategory,
            spSelectPais, spSelectDepartamento,spSelectMunicipio,
            tvCrearArticuloPoint, tvCrearArticuloRadius)

        //ivArticuloImageにセットされたuriをMultipartBody.Partオブジェクトに変換する
        var part1 = makeImgagePartForRetrofit(imageView1FilePath, IMAGE1)
        var part2 = makeImgagePartForRetrofit(imageView2FilePath, IMAGE2)
        var part3 = makeImgagePartForRetrofit(imageView3FilePath, IMAGE3)

        val reqBody : RequestBody = RequestBody.create(MediaType.parse("application/json"), Gson().toJson(retrievedItemObj))


        val service = setService()
        service.patchItemDetailSerializerAPIView(itemObj!!.id.toString(), sessionData.authTokenHeader!!, part1, part2, part3, reqBody ).enqueue(object :Callback<ResultModel>{

            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                progressBar.visibility = View.GONE
                println("onResponseを通る : EditarFragment#patchItemDetailSerializerAPIView")
                println(call.request().body())

                //EsitarArticuloFragmentを切る
                listener!!.successCrearArticulo()
            }

            override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                println("onFailureを通る : EditarArticuloFragment#patchItemDetailSerializerAPIView")
                println(call.request().body())
                println(t)
                println(t.message)
                progressBar.visibility = View.GONE
                makeToast(MyApplication.appContext, getString(R.string.fail_editar_articulo))
            }

        })

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