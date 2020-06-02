package ga.sharexela.sharexela

import android.Manifest
import android.content.Context
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

    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    val REQUEST_CODE_PERMISSIONS = 15




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


        //具体的な座標データを利用する
        btnCrearArticuloLaunchMap.setOnClickListener {

            //位置情報のパーミッション状態を取得する
            var permissionStatus = isAllPermissionsGranted(REQUIRED_PERMISSIONS)

            if (permissionStatus == true) return@setOnClickListener showMap()
            // パーミッション許可をリクエストし、許可が出ればshowMap()が実行される
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }



        //btnCrearArticuloのリスナーセット
        btnCrearArticulo.setOnClickListener {
            //サーバーにデータ送信
            postCrearArticuloData()
        }

        //画像をタップするとギャラリーからデータを引っ張る
        ivArticuloImage1.setOnClickListener { listener!!.onLaunchImagesActivity() }
        ivArticuloImage2.setOnClickListener { listener!!.onLaunchImagesActivity() }
        ivArticuloImage3.setOnClickListener { listener!!.onLaunchImagesActivity() }



        //Regionデータをareing.xmlから取得してRegionをセット
        setRegionSpinner(itemObj)

    }



    fun setRegionSpinner(itemObj: ItemSerializerModel?){

        setAdapterToPaisSpinner(spSelectPais)
        setAdapterToDepartamentoSpinner(spSelectDepartamento)
        setAdapterToMunicipioSpinner(spSelectMunicipio)

        if (itemObj == null){
            setValueToPaisSpinner(sessionData.profileObj!!.adm0!!, spSelectPais)
            setValueToDepartamentoSpinner(sessionData.profileObj!!.adm1!!, spSelectDepartamento)
            setValueToMunicipioSpinner(sessionData.profileObj!!.adm2!!, spSelectMunicipio)
            return
        }
        setValueToPaisSpinner(itemObj.adm0!!, spSelectPais)
        setValueToDepartamentoSpinner(itemObj.adm1!!, spSelectDepartamento)
        setValueToMunicipioSpinner(itemObj.adm2!!, spSelectMunicipio)
        return

    }






    override fun onResume() {

        super.onResume()
        if (itemObj == null) return

        //画面に再反映させる
        setValueToCategotySpinner(itemObj!!, spArticuloCategory)
        etArticuloTitle.setText(itemObj!!.title)
        etArticuloDescription.setText(itemObj!!.description)
        tvCrearArticuloPoint.text = itemObj?.point
        tvCrearArticuloRadius.text = itemObj!!.radius.toString()
        setRegionSpinner(itemObj)
        if (imageView1FilePath != null && imageView1FilePath != "") Glide.with(this).load(imageView1FilePath).into(ivArticuloImage1)

        if (imageView2FilePath != null && imageView2FilePath != "") Glide.with(this).load(imageView2FilePath).into(ivArticuloImage2)

        if (imageView3FilePath != null && imageView3FilePath != "") Glide.with(this).load(imageView3FilePath).into(ivArticuloImage3)

        //pointがある場合にはgoogleMapsに描画する
        frameLayoutCrearMap.visibility = View.GONE
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
                etArticuloTitle, etArticuloDescription, spArticuloCategory,
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


        val retrievedItemObj = retrieveArticuloData(
                etArticuloTitle, etArticuloDescription, spArticuloCategory,
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

        if (requestCode == REQUEST_CODE_PERMISSIONS) {

            if (isAllPermissionsGranted(REQUIRED_PERMISSIONS) != true) {

                makeToast(MyApplication.appContext, "Permissions not granted by the user.")
                return
            }
            showMap()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //描画したいこと mapの縮尺, mapの中心設定, pointまたはradiusの描画
        //mapの縮尺
        if (itemObj?.point == null) return
        val latLng = getLatLng(itemObj!!)
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



//CrearArticuloFragmentとEditarArticuloFragmentで使用している
fun makeImgagePartForRetrofit(imageViewFilePath:String?, formName:String): MultipartBody.Part?{
    var part:MultipartBody.Part? = null

    if (imageViewFilePath != ""){

        try {
            var file = File(imageViewFilePath)

            println("FILEの内容チェック")
            println(file)

            var fileBody = RequestBody.create(MediaType.parse("image/*"), file)
            part = MultipartBody.Part.createFormData(formName, file.name, fileBody)
        }catch (e:NullPointerException){
            println(e)
        }
    }

    println("part1の標準出力をじっこう")
    println(part == null)
    println(imageViewFilePath)
    return part
}



//CrearArticuloFragmentとEditarArticuloFragmentで使用している
fun retrieveArticuloData(etArticuloTitle:EditText, etArticuloDescription: EditText,
                         spArticuloCategory: Spinner, spSelectPais:Spinner, spSelectDepartamento:Spinner,
                         spSelectMunicipio:Spinner, tvCrearArticuloPoint:TextView, tvCrearArticuloRadius: TextView)
        :ItemSerializerModel {

        //入力データの取得 //なんでpoontとradiusを追加していないのか理由がわかっていない。。。
        val title           = etArticuloTitle.text.toString()
        val description     = etArticuloDescription.text.toString()
        val category:String = spArticuloCategory.selectedItem.toString()
        val adm0: String    = spSelectPais.selectedItem.toString()
        val adm1: String    = spSelectDepartamento.selectedItem.toString()
        val adm2: String    = spSelectMunicipio.selectedItem.toString()
        val point: String   = tvCrearArticuloPoint.text.toString()
        val radius: Int     = tvCrearArticuloRadius.text.toString().toInt()
        //CategorySerializerModelオブジェクトの生成
        val categoryObj     = CategorySerializerModel(name=category)
        //ItemSerializerModelオブジェクトの作成
        val itemObj = ItemSerializerModel(title=title, description=description, category=categoryObj, adm0=adm0, adm1=adm1, adm2=adm2, point=point, radius=radius )

        return itemObj
    }



fun setAdapterToCategotySpinner(spinner: Spinner){
    val adapter = ArrayAdapter.createFromResource(MyApplication.appContext, R.array.categoryList, android.R.layout.simple_list_item_1)
    //spArticuloCategory.adapter = adapter
    spinner.adapter = adapter
}

fun setValueToCategotySpinner(itemObj: ItemSerializerModel, spinner:Spinner) {
    val categoryList: Array<String> = MyApplication.appContext.resources.getStringArray(R.array.categoryList)
    val indexOfCategory = categoryList.indexOf(itemObj.category!!.name)
    //spArticuloCategory.setSelection(indexOfCategory)
    spinner.setSelection(indexOfCategory)
}

fun setAdapterToPaisSpinner(spinner: Spinner){
    // R.id.spSelectPais用の関数
    val adapter = ArrayAdapter.createFromResource(MyApplication.appContext, R.array.paisList, android.R.layout.simple_list_item_1)
    spinner.adapter = adapter
}

fun setValueToPaisSpinner(strPais: String, spinner:Spinner) {
    val paisList: Array<String> = MyApplication.appContext.resources.getStringArray(R.array.paisList)
    val indexOfPais = paisList.indexOf(strPais)
    spinner.setSelection(indexOfPais)
}

fun setAdapterToDepartamentoSpinner(spinner: Spinner){
    //R.id.spSelectDepartamento用の関数
    val adapter = ArrayAdapter.createFromResource(MyApplication.appContext, R.array.departamentoList, android.R.layout.simple_list_item_1)
    spinner.adapter = adapter
}

fun setValueToDepartamentoSpinner(strDepartamento: String, spinner:Spinner) {
    val departamentoList: Array<String> = MyApplication.appContext.resources.getStringArray(R.array.departamentoList)
    val indexOfDepartamentos = departamentoList.indexOf(strDepartamento)
    spinner.setSelection(indexOfDepartamentos)
}

fun setAdapterToMunicipioSpinner(spinner: Spinner){
    //R.id.spSelectMunicipio用の関数
    val adapter = ArrayAdapter.createFromResource(MyApplication.appContext, R.array.municipioList, android.R.layout.simple_list_item_1)
    spinner.adapter = adapter
}

fun setValueToMunicipioSpinner(strMunicipio: String, spinner:Spinner) {
    val municipioList: Array<String> = MyApplication.appContext.resources.getStringArray(R.array.municipioList)
    val indexOfMunicipos = municipioList.indexOf(strMunicipio)
    spinner.setSelection(indexOfMunicipos)
}




