package com.example.takayama

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_crear_articulo.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.URI

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CrearArticuloFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CrearArticuloFragment.newInstance] factory method to
 * create an instance of this fragment.
 */




class CrearArticuloFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_crear_articulo, container, false)
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        //spinner(Articuloの記事カテゴリ)の選択肢をセットする
        val adapter = ArrayAdapter.createFromResource(MyApplication.appContext, R.array.categoryList, android.R.layout.simple_list_item_1)
        spArticuloCategory.adapter = adapter


        //btnCrearArticuloのリスナーセット
        btnCrearArticulo.setOnClickListener {
            //サーバーにデータ送信
            postCrearArticuloData()
        }


        ivArticuloImage1.setOnClickListener { listener!!.onClickImageView(R.id.ivArticuloImage1) }
        ivArticuloImage2.setOnClickListener { listener!!.onClickImageView(R.id.ivArticuloImage2) }
        ivArticuloImage3.setOnClickListener { listener!!.onClickImageView(R.id.ivArticuloImage3) }


        //Regionデータを取得してRegionをセット
        val service = setService()
        service.getAreaSettingsAPIView().enqueue(object :Callback<RegionListSet>{

            override fun onResponse(call: Call<RegionListSet>, response: Response<RegionListSet>) {
                println("onResponse を通る")

                val adm0ArrayList: ArrayList<String> = response.body()!!.ADM0_LIST
                val adm1ArrayList: ArrayList<String> = response.body()!!.ADM1_LIST
                val adm2ArrayList: ArrayList<String> = response.body()!!.ADM2_LIST

                spSelectPais.adapter = ArrayAdapter(MyApplication.appContext, android.R.layout.simple_list_item_1, adm0ArrayList)
                spSelectDepartamento.adapter = ArrayAdapter(MyApplication.appContext, android.R.layout.simple_list_item_1, adm1ArrayList)
                spSelectMunicipio.adapter = ArrayAdapter(MyApplication.appContext, android.R.layout.simple_list_item_1, adm2ArrayList)


            }

            override fun onFailure(call: Call<RegionListSet>, t: Throwable) {
                println("onFailure を通る")
                println(t)
            }
        })


    }

    private fun postCrearArticuloData() {


        //入力データの取得
        val title = etArticuloTitle.text.toString()
        val description = etArticuloDescription.text.toString()
        val category:String = spArticuloCategory.selectedItem.toString()
        //println("CATEGORY : " + category)

        val adm0: String = spSelectPais.selectedItem.toString()
        val adm1: String = spSelectDepartamento.selectedItem.toString()
        val adm2: String = spSelectMunicipio.selectedItem.toString()

        val categoryObj = CategorySerializerModel(name=category)

        //タイトルや説明欄に入力データデータがないときにメッセージを表示する
        if (title == "" || description == "") {
            makeToast(MyApplication.appContext, getString(R.string.title_o_description_blank_message))
            return
        }
        val itemObj = ItemSerializerModel(title=title, description=description, category=categoryObj, adm0=adm0, adm1=adm1, adm2=adm2 )



        //ivArticuloImageにセットされたuriをMultipartBody.Partオブジェクトに変換する
        var part1:MultipartBody.Part? = null
        if (uri1 != null) {
            var filePath1 = getPathFromUri(MyApplication.appContext, uri1!!)
            var file1 = File(filePath1)
            var fileBody1 = RequestBody.create(MediaType.parse("image/*"), file1)
            part1 = MultipartBody.Part.createFormData(IMAGE1, file1.name, fileBody1)
        }

        var part2:MultipartBody.Part? = null
        if (uri2 != null){
            var filePath2 = getPathFromUri(MyApplication.appContext, uri2!!)
            var file2 = File(filePath2)
            var fileBody2 = RequestBody.create(MediaType.parse("image/*"), file2)
            part2 = MultipartBody.Part.createFormData(IMAGE2, file2.name, fileBody2)
        }

        var part3:MultipartBody.Part? = null
        if (uri3 != null){
            var filePath3 = getPathFromUri(MyApplication.appContext, uri3!!)
            var file3 = File(filePath3)
            var fileBody3 = RequestBody.create(MediaType.parse("image/*"), file3)
            part3 = MultipartBody.Part.createFormData(IMAGE3, file3.name, fileBody3)
        }

        println("part1"+part1.toString())
        println("part2"+part2.toString())
        println("part3"+part3.toString())





        //fileパスについて問題があった。

        //val filePath = "/storage/self/primary/DCIM/Camera/IMG_20200330_150330.jpg"


        //val path = filePath.toString()
        //val file = File(path)



        //val fileBody = RequestBody.create(MediaType.parse("image/*"), file)
        //val part = MultipartBody.Part.createFormData(IMAGE1, file.name, fileBody)
        //val part1 = MultipartBody.Part.createFormData(IMAGE2, file.name, fileBody)
        //val part2 = MultipartBody.Part.createFormData(IMAGE3, file.name, fileBody)


        //val reqBody :RequestBody = RequestBody.create(MediaType.parse("text/plain"), "YYYYYSDSSSSSSSSSSSDSCSCe")
        //val reqBody :RequestBody = RequestBody.create(MediaType.parse("application/json"), "{'active': True, 'adm0': 'GUATEMALA', 'adm1': 'Alta Verapaz', 'adm2': 'Acatenango', 'category': {'name': 'Donar o vender'}, 'description': 'huhuhuhko', 'title': 'kokokok'}")
        val reqBody :RequestBody = RequestBody.create(MediaType.parse("application/json"), Gson().toJson(itemObj))
        //val authTokenHeader = " Token ebfa00bd84de2b8f319b747636270257ec24601c"
        val authTokenHeader = " Token " + authToken

        val service = setService()
        //service.postItemCreateAPIView(authTokenHeader=authTokenHeader, itemObj=itemObj).enqueue(object :Callback<ResultModel>{
        service.postItemCreateAPIViewMultiPart(authTokenHeader=authTokenHeader, file1=part1, file2 = part2, file3 = part3, requestBody=reqBody).enqueue(object :Callback<ResultModel>{
            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                println("onResponseを通る")
                println(call.request().body())

            }

            override fun onFailure(call: Call<ResultModel>, t: Throwable) {

                println("onFailureを通る")

                //println(call.request().body())
                println(call.request().headers())
                println(t)
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

        //ImageViewをクリックしてギャラリーから画像データを呼び出す。
        fun onClickImageView(imageViewId:Int)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CrearArticuloFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CrearArticuloFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}