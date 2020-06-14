package ga.sharexela.sharexela

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_my_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response




private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"





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
        menu.findItem(R.id.menuGoHome).isVisible = false
        menu.findItem(R.id.action_settings).isVisible = false
        menu.findItem(R.id.menuDone).isVisible = false
        menu.findItem(R.id.menuSync).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        //when (item.itemId) {
            //R.id.menuGoHome ->{ item.isVisible = false )
        //}
        return true
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
                    var price = itemSerializerList[numero].price
                    var description = itemSerializerList[numero].description
                    var categoryNumber = itemSerializerList[numero].category!!.number
                    //var created_at = itemSerializerList[numero].created_at
                    var image = itemSerializerList[numero].image1

                    dataArrayList.add(
                        ItemSerializerModel(
                            id = id,
                            title = title,
                            price = price,
                            description = description,
                            category = CategorySerializerModel(number=categoryNumber ),
                            //created_at = created_at,
                            image1 = image
                        ))
                }

                val divider = androidx.recyclerview.widget.DividerItemDecoration(MyApplication.appContext, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL)
                recyclerViewMyLsit.apply { addItemDecoration(divider) }

                val layoutManager = LinearLayoutManager(this@MyListFragment.context)
                recyclerViewMyLsit.layoutManager = layoutManager

                val adapter = MyItemVerticalCardRecyclerViewAdapter(dataArrayList=dataArrayList, myListener=listener, favListener=null )
                recyclerViewMyLsit.adapter = adapter

            }

            override fun onFailure(call: Call<MyItemListSerializerAPIView>, t: Throwable) {

            }

        })

    }


    interface OnFragmentInteractionListener {
        fun launchDetailActivity(selectedItem: ItemSerializerModel)
    }

    companion object {

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
