package com.example.takayama

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_my_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MyListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MyListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyListFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_my_list, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.menuSearch).isVisible = false
        menu.findItem(R.id.menuGoHome).isVisible = true
        menu.findItem(R.id.action_settings).isVisible = false
        menu.findItem(R.id.menuDone).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menuGoHome ->{
                //MyListFragmentを消去してMasterFragmentを起動し直す
                //あとで
            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //val adapter = MyRecyclerViewAdapter()
        //recyclerViewMyLsit.adapter = adapter
        getMyItemList()

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



    private fun getMyItemList() {

        val service = setService()
        service.getMyItemListAPIView(sessionData.authTokenHeader!!).enqueue(object : Callback<MyItemListSerializerAPIView>{

            override fun onResponse(call: Call<MyItemListSerializerAPIView>, response: Response<MyItemListSerializerAPIView>) {
                println("onResponceを通る")


                var dataArrayList: ArrayList<ItemSerializerModel> = arrayListOf()
                var itemSerializerList:List<ItemSerializerModel>? = response.body()?.itemSerializer

                for (numero in 0..itemSerializerList!!.size-1){

                    //println("番号 : "+ numero)
                    var id    = itemSerializerList[numero].id
                    var title = itemSerializerList[numero].title
                    //var description = itemSerializerList[numero].description
                    //var category = itemSerializerList[numero].category.name
                    //var created_at = itemSerializerList[numero].created_at
                    var image = itemSerializerList[numero].image1

                    dataArrayList.add(
                        ItemSerializerModel(
                            id = id,
                            title = title,
                            //description = description,
                            //category = category,
                            //created_at = created_at,
                            image1 = image
                        ))

                }


                val layoutManager = LinearLayoutManager(this@MyListFragment.context)
                recyclerViewMyLsit.layoutManager = layoutManager

                val adapter = MyListRecyclerViewAdapter(dataArrayList=dataArrayList, myListener=listener)
                recyclerViewMyLsit.adapter = adapter

            }

            override fun onFailure(call: Call<MyItemListSerializerAPIView>, t: Throwable) {
                //TODO("Not yet implemented")
            }

        })

    }


    interface OnFragmentInteractionListener {
        fun launchDetailActivity(selectedItem: ItemSerializerModel)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
