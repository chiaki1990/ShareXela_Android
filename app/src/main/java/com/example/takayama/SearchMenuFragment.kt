package com.example.takayama

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_search_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



//このSearchを追加したことでMainActivity, MainFragment, activity_main.xml, app_bar_main.xml, content_main.xml, fragment_main.xml, nav_header_master.xml
//これらを消去する。。。



class SearchMenuFragment : Fragment() {
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_search_menu, container, false)
        setHasOptionsMenu(true)
        return view
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //各buttonのリスナーをセットする

        btnDonarGuatemala.setOnClickListener { excuteGetItemDonarListAPIView() }
        btnDonarDepartamento.setOnClickListener { executeGetItemDonarLocalListAPIView() }
        btnAyudaGuatemala.setOnClickListener { executeGetItemAyudaListAPIView() }
        btnAyudaDepartamento.setOnClickListener { executeGetItemAyudaLocalAPIView() }
        btnAnunciateGuatemala.setOnClickListener { executeGetItemAnuncioListAPIView() }
        btnAnunciateDepartament.setOnClickListener { executeGetItemAnuncioLocalListAPIView() }
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
        fun launchMasterActivity(itemObjectsSerialized:ItemObjectsSerialized, stringItemObjectsCategory: String)

    }

    companion object {


        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    private fun excuteGetItemDonarListAPIView() {
        //クエリ結果を取得して、MasterFragmentを起動する。
        //ただしフラグメントの起動はSearchActivityで起動するのでコールバックすることになる。

        val service = setService()
        service.getItemDonarListAPIView().enqueue(object : Callback<ItemUniversalListAPIView>{

            override fun onResponse(call: Call<ItemUniversalListAPIView>, response: Response<ItemUniversalListAPIView>) {
                println("onResponseを通る_SearchMenuFragment#excuteGetItemDonarListAPIView")
                //クエリ結果を取得し、それを引数としてコールバックする。
                val itemObjects:List<ItemSerializerModel> = response.body()!!.ITEM_OBJECTS
                if (itemObjects.size == 0){
                    makeToast(MyApplication.appContext, getString(R.string.toast_message_no_article))
                    return
                }
                val itemObjectsSerialized:ItemObjectsSerialized = ItemObjectsSerialized(itemObjects=itemObjects)
                val itemObjectsCategory = ItemObjectsCategory.DONAR_GUATEMALA.name
                listener!!.launchMasterActivity(itemObjectsSerialized, itemObjectsCategory)
            }

            override fun onFailure(call: Call<ItemUniversalListAPIView>, t: Throwable) {
                println("onFailureを通る_SearchMenuFragment#excuteGetItemDonarListAPIView")
                println(t)
            }
        })
    }

    private fun executeGetItemAyudaListAPIView(){
        //クエリ結果を取得して、MasterFragmentを起動する。
        //ただしフラグメントの起動はSearchActivityで起動するのでコールバックすることになる。

        val service = setService()
        service.getItemAyudaListAPIView().enqueue(object :Callback<ItemUniversalListAPIView>{

            override fun onResponse(call: Call<ItemUniversalListAPIView>, response: Response<ItemUniversalListAPIView>) {
                println("onResponseを通る_SearchMenuFragment#excuteGetItemAyudaListAPIView")
                //クエリ結果を取得し、それを引数としてコールバックする。

                println("API叩かれている？")
                println(response.body())

                val itemObjects:List<ItemSerializerModel> = response.body()!!.ITEM_OBJECTS
                if (itemObjects.size == 0){
                    makeToast(MyApplication.appContext, getString(R.string.toast_message_no_article))
                    return
                }
                val itemObjectsSerialized:ItemObjectsSerialized = ItemObjectsSerialized(itemObjects=itemObjects)
                val itemObjectsCategory = ItemObjectsCategory.AYUDAR_GUATEMALA.name
                listener!!.launchMasterActivity(itemObjectsSerialized, itemObjectsCategory)
            }

            override fun onFailure(call: Call<ItemUniversalListAPIView>, t: Throwable) {
                println("onFailureを通る_SearchMenuFragment#excuteGetItemAyudaListAPIView")
                println(t)
            }
        })
    }

    private fun executeGetItemAnuncioListAPIView(){

        val service = setService()
        service.getItemAnuncioListAPIView().enqueue(object :Callback<ItemUniversalListAPIView>{

            override fun onResponse(call: Call<ItemUniversalListAPIView>, response: Response<ItemUniversalListAPIView>) {
                println("onResponseを通る_SearchMenuFragment#excuteGetItemAnuncioListAPIView")
                //クエリ結果を取得し、それを引数としてコールバックする。

                //println("API叩かれている？")
                //println(response.body())

                val itemObjects:List<ItemSerializerModel> = response.body()!!.ITEM_OBJECTS
                if (itemObjects.size == 0){
                    makeToast(MyApplication.appContext, getString(R.string.toast_message_no_article))
                    return
                }
                val itemObjectsSerialized:ItemObjectsSerialized = ItemObjectsSerialized(itemObjects=itemObjects)
                val itemObjectsCategory = ItemObjectsCategory.ANUNCIO_GUATEMALA.name
                listener!!.launchMasterActivity(itemObjectsSerialized, itemObjectsCategory)

            }

            override fun onFailure(call: Call<ItemUniversalListAPIView>, t: Throwable) {
                println("onFailureを通る_SearchMenuFragment#excuteGetItemAyudaListAPIView")
                println(t)
            }
        })
    }


    private fun executeGetItemDonarLocalListAPIView(){
        val service = setService()
        if (sessionData.authTokenHeader == null){
            makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn))
            return
        }

        service.getItemDonarLocalListAPIView(sessionData.authTokenHeader!!).enqueue(object :Callback<ItemUniversalListAPIView>{

            override fun onResponse(call: Call<ItemUniversalListAPIView>, response: Response<ItemUniversalListAPIView>) {
                println("onResponseを通る_SearchMenuFragment#excuteGetItemDonarLocalListAPIView")
                //クエリ結果を取得し、それを引数としてコールバックする。

                val itemObjects:List<ItemSerializerModel> = response.body()!!.ITEM_OBJECTS
                if (itemObjects.size == 0){
                    makeToast(MyApplication.appContext, getString(R.string.toast_message_no_article))
                    return
                }
                val itemObjectsSerialized:ItemObjectsSerialized = ItemObjectsSerialized(itemObjects=itemObjects)
                val itemObjectsCategory = ItemObjectsCategory.DONAR_LOCAL.name
                listener!!.launchMasterActivity(itemObjectsSerialized, itemObjectsCategory)
            }

            override fun onFailure(call: Call<ItemUniversalListAPIView>, t: Throwable) {

                println("onFailureを通る_SearchMenuFragment#excuteGetItemAyudaListAPIView")
                println(t)
            }
        })
    }


    private fun executeGetItemAyudaLocalAPIView(){
        val service = setService()
        if (sessionData.authTokenHeader == null){
            makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn))
            return
        }
        service.getItemAyudaLocalListAPIView(sessionData.authTokenHeader!!).enqueue(object:Callback<ItemUniversalListAPIView>{

            override fun onResponse(call: Call<ItemUniversalListAPIView>, response: Response<ItemUniversalListAPIView>) {
                println("onResponseを通る_SearchMenuFragment#excuteGetItemAyudaLocalListAPIView")
                //クエリ結果を取得し、それを引数としてコールバックする。

                val itemObjects:List<ItemSerializerModel> = response.body()!!.ITEM_OBJECTS
                if (itemObjects.size == 0){
                    makeToast(MyApplication.appContext, getString(R.string.toast_message_no_article))
                    return
                }
                val itemObjectsSerialized:ItemObjectsSerialized = ItemObjectsSerialized(itemObjects=itemObjects)
                val itemObjectsCategory = ItemObjectsCategory.AYUDAR_LOCAL.name
                listener!!.launchMasterActivity(itemObjectsSerialized, itemObjectsCategory)
            }

            override fun onFailure(call: Call<ItemUniversalListAPIView>, t: Throwable) {
                println("onFailureを通る_SearchMenuFragment#excuteGetItemAyudaLocalListAPIView")
                println(t)
            }
        })
    }

    private fun executeGetItemAnuncioLocalListAPIView(){
        val service = setService()
        if (sessionData.authTokenHeader == null){
            makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn))
            return
        }
        service.getItemAnuncioLocalListAPIView(sessionData.authTokenHeader!!).enqueue(object :Callback<ItemUniversalListAPIView>{

            override fun onResponse(call: Call<ItemUniversalListAPIView>, response: Response<ItemUniversalListAPIView>) {
                println("onResponseを通る_SearchMenuFragment#excuteGetItemAnuncioLocalListAPIView")
                //クエリ結果を取得し、それを引数としてコールバックする。

                val itemObjects:List<ItemSerializerModel> = response.body()!!.ITEM_OBJECTS
                if (itemObjects.size == 0){
                    makeToast(MyApplication.appContext, getString(R.string.toast_message_no_article))
                    return
                }
                val itemObjectsSerialized:ItemObjectsSerialized = ItemObjectsSerialized(itemObjects=itemObjects)
                val itemObjectsCategory = ItemObjectsCategory.ANUNCIO_LOCAL.name
                listener!!.launchMasterActivity(itemObjectsSerialized, itemObjectsCategory)
            }

            override fun onFailure(call: Call<ItemUniversalListAPIView>, t: Throwable) {
                println("onFailureを通る_SearchMenuFragment#excuteGetItemAnuncioLocalListAPIView")
                println(t)
            }
        })
    }

}
