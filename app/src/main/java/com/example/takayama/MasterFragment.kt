package com.example.takayama

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_master.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




class MasterFragment : Fragment() {

    private var itemObjectsSerialized: ItemObjectsSelialized? = null
    private var param2: String? = null

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemObjectsSerialized = it.getSerializable(ARG_PARAM1) as ItemObjectsSelialized
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        menu.apply {
            findItem(R.id.menuSearch).isVisible = true
            findItem(R.id.menuGoHome).isVisible = false
            findItem(R.id.action_settings).isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menuSearch -> {
                listener!!.onSearchMenuSelected()
            }

        }
        return true
    }




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_master, container, false)
        setHasOptionsMenu(true)
        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //getItemDarListView()

        //List<RowModel>を生成する
        var dataArrayList: ArrayList<ItemModel> = arrayListOf()

        val itemObjects = itemObjectsSerialized!!.itemObjects

        for (numero in 0..itemObjects!!.size-1){
            //for (numero in 0..item_objects_count){
            //println("番号 : "+ numero)
            //println(itemSerializerList[numero])
            var id    = itemObjects[numero].id
            var title = itemObjects[numero].title
            //var description = itemSerializerList[numero].description
            //var category = itemSerializerList[numero].category.name
            //var created_at = itemSerializerList[numero].created_at
            var image = itemObjects[numero].image1

            dataArrayList.add(
                ItemModel(
                    id = id,
                    title = title,
                    //description = description,
                    //category = category,
                    //created_at = created_at,
                    image = image
                ))

        }

        val layoutManager = GridLayoutManager(this@MasterFragment.context, 3)
        recyclerView.layoutManager = layoutManager

        val adapter = MyRecyclerViewAdapter(dataArrayList=dataArrayList, myListener=listener)
        recyclerView.adapter = adapter





    }

    //不要な関数となってしまった。。。
    private fun getItemDarListView() {


        val service = setService()
        service.getItemListAPIView().enqueue(object : Callback<ItemListAPIViewModel> {


            override fun onResponse(call: Call<ItemListAPIViewModel>, response: Response<ItemListAPIViewModel>) {

                //List<RowModel>を生成する
                var dataArrayList: ArrayList<ItemModel> = arrayListOf()

                //responseを解析する
                //println("RESPONSEの解析")
                //println(response.body())

                var itemObjects:List<ItemSerializerModel> = response.body()?.ITEM_OBJECTS!!


                for (numero in 0..itemObjects!!.size-1){
                    //for (numero in 0..item_objects_count){
                    //println("番号 : "+ numero)
                    //println(itemSerializerList[numero])
                    var id    = itemObjects[numero].id
                    var title = itemObjects[numero].title
                    //var description = itemSerializerList[numero].description
                    //var category = itemSerializerList[numero].category.name
                    //var created_at = itemSerializerList[numero].created_at
                    var image = itemObjects[numero].image1

                    dataArrayList.add(
                        ItemModel(
                            id = id,
                            title = title,
                            //description = description,
                            //category = category,
                            //created_at = created_at,
                            image = image
                        ))

                }

                val layoutManager = GridLayoutManager(this@MasterFragment.context, 3)
                recyclerView.layoutManager = layoutManager

                val adapter = MyRecyclerViewAdapter(dataArrayList=dataArrayList, myListener=listener)
                recyclerView.adapter = adapter


            }


            override fun onFailure(call: Call<ItemListAPIViewModel>, t: Throwable) {
                println("onFailureの結果　：　")
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
        //使用確認済み
        fun onSearchMenuSelected()

        fun launchDetailActivity(selectedItem: ItemModel)


    }

    companion object {

        @JvmStatic
        fun newInstance(itemObjectsSerialized: ItemObjectsSelialized?, param2: String) =
            MasterFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, itemObjectsSerialized)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}
